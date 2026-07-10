# cog-gen.ps1 -- materialize a build cell's gen/ tree from the one shared source.
# Usage: pwsh -File scripts\cog-gen.ps1 -Cell Fabric/1.21.8
# Every cell -- pre-26 AND the 26 line -- runs cog-gen (the 26 twin was merged out 2026-07-09).
# gen/ is disposable build output (gitignored). Edit ONLY _codegen/cog_sources + shared_minecraft.
param(
    [Parameter(Mandatory)][string]$Cell,           # <Loader>/<mcver>, e.g. Fabric/1.21.8
    [string]$SrcLoader,                            # override source flavour (NeoForge 1.20.1-1.20.4 SRG cells are forge-shaped)
    [string]$McVerArg                              # explicit MC version (the '26' cell dir name is not a concrete version)
)
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path $PSScriptRoot -Parent
$parts  = $Cell -split '[/\\]'
$LoaderDir = $parts[0]; $McVer = $parts[1]
if ($McVerArg) { $McVer = $McVerArg }
$McVerNum = ($McVer -replace '-.*$','')   # 26.3-snapshot-3 -> 26.3 for version parsing
$Loader = $LoaderDir.ToLower()                     # fabric | forge | neoforge
if ($SrcLoader) { $Loader = $SrcLoader.ToLower() } # e.g. -SrcLoader forge for the SRG NeoForge cells
$cg  = Join-Path $repoRoot '_codegen'
$cs  = Join-Path $cg 'cog_sources'
$cell = Join-Path $repoRoot ($LoaderDir + '\' + $parts[1])   # path uses the literal cell dir ('26'); $McVer holds the concrete version
if (-not (Test-Path $cell)) { throw "cell not found: $cell" }
$gen  = Join-Path $cell 'gen'
$pkg  = 'com\kishku7\lavaboats'
$genJ = Join-Path $gen ('src\main\java\' + $pkg)
$genR = Join-Path $gen 'src\main\resources'

$v = [version]$McVerNum
$legacy   = $v -lt [version]'1.21.2'               # boat-subclass era (converged via BoatDropMixin)
$renamed  = $v -ge [version]'1.21.11'              # Identifier / vehicle.boat / depth-strider era
$java17   = $v -lt [version]'1.20.5'               # JDK17 sub-line (1.20.1-1.20.4)
$is26     = $v -ge [version]'26.0'                 # 26.x line (unified onto cog-gen)

# ---- 1. wipe gen/, copy shared_minecraft java verbatim ----
Remove-Item $gen -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $genJ, $genR | Out-Null
Copy-Item (Join-Path $repoRoot ('shared_minecraft\src\main\java\' + $pkg + '\*')) $genJ -Recurse -Force

# ---- 2. overwrite drift files with the cog-instrumented shared copies ----
Copy-Item (Join-Path $cs 'shared\*') $genJ -Recurse -Force

# ---- 3. loader-specific files ----
$L = Join-Path $cs $Loader
Get-ChildItem $L -File -Filter *.java | ForEach-Object { Copy-Item $_.FullName $genJ -Force }   # ModEntities/ModItems/Platform
if (Test-Path (Join-Path $L $Loader)) {                                                        # fabric/ forge/ neoforge/ subpackage
    Copy-Item (Join-Path $L $Loader) (Join-Path $genJ $Loader) -Recurse -Force
}

# ---- 4. era presence ----
if ($legacy) {
    Copy-Item (Join-Path $cs 'shared_legacy\*') $genJ -Recurse -Force
    Remove-Item (Join-Path $genJ 'client\LavaBoatLayers.java') -Force -ErrorAction SilentlyContinue
}
if (-not $renamed) {
    Remove-Item (Join-Path $genJ 'mixin\LavaDepthStriderMixin.java') -Force -ErrorAction SilentlyContinue
}

# ---- 5. resources: generated per version + textures/icon from shared ----
& python (Join-Path $cg 'gen_resources.py') $McVerNum $genR
if ($LASTEXITCODE -ne 0) { throw "gen_resources.py failed for $McVer" }
$shR = Join-Path $repoRoot 'shared_minecraft\src\main\resources'
New-Item -ItemType Directory -Force -Path (Join-Path $genR 'assets\lavaboats') | Out-Null
Copy-Item (Join-Path $shR 'assets\lavaboats\textures') (Join-Path $genR 'assets\lavaboats\textures') -Recurse -Force
Copy-Item (Join-Path $shR 'assets\lavaboats\icon.png') (Join-Path $genR 'assets\lavaboats\icon.png') -Force
# ---- 5b. pack.mcmeta (per-version resource pack_format; authoritative: Memory/knowledge/pack-formats.md) ----
$packFormats = @{
    '1.20.1'=15; '1.20.2'=18; '1.20.3'=22; '1.20.4'=22; '1.20.5'=32; '1.20.6'=32;
    '1.21'=34; '1.21.1'=34; '1.21.2'=42; '1.21.3'=42; '1.21.4'=46; '1.21.5'=55;
    '1.21.6'=63; '1.21.7'=64; '1.21.8'=64; '1.21.9'=69; '1.21.10'=69; '1.21.11'=75
}
if ($is26) {
    # 26.x: range-form (min=max=n); value injected per-26.X by processResources ${packFormat} (PACK_FORMAT env).
    ('{"pack":{"description":"Lava Boats resources","pack_format":${packFormat},"min_format":${packFormat},"max_format":${packFormat}}}') |
        Set-Content (Join-Path $genR 'pack.mcmeta') -Encoding UTF8
} else {
    $pf = $packFormats[$McVerNum]
    if (-not $pf) { throw "no pack_format for $McVerNum -- extend the table (knowledge/pack-formats.md)" }
    ('{"pack":{"description":"Lava Boats resources","pack_format":' + $pf + '}}') |
        Set-Content (Join-Path $genR 'pack.mcmeta') -Encoding UTF8
}

# ---- 6. mixins jsons (single source of truth for the era rules) ----
$compat = if ($is26) { 'JAVA_25' } elseif ($java17 -or ($Loader -ne 'fabric' -and $legacy)) { 'JAVA_17' } else { 'JAVA_21' }   # 26=JAVA_25; Forge51/neo21.0 Mixin 0.8.5 (max JAVA_17)
$mx = '"EntityFireImmuneMixin",' + "`n    " + '"ItemEntityLavaFloatMixin"'
if ($legacy)      { $mx += ',' + "`n    " + '"BoatDropMixin"' }
elseif ($renamed) { $mx += ',' + "`n    " + '"LavaDepthStriderMixin"' }
$refmap = ''
if ($Loader -eq 'forge' -and $java17) { $refmap = "`n  ""refmap"": ""lavaboats.refmap.json""," }   # classic SRG Forge only
@"
{
  "required": true,
  "minVersion": "0.8",$refmap
  "package": "com.kishku7.lavaboats.mixin",
  "compatibilityLevel": "$compat",
  "injectors": {
    "defaultRequire": 1
  },
  "mixins": [
    $mx
  ]
}
"@ | Set-Content (Join-Path $genR 'lavaboats-common.mixins.json') -Encoding UTF8
@"
{
  "required": true,
  "minVersion": "0.8",$refmap
  "package": "com.kishku7.lavaboats.mixin",
  "compatibilityLevel": "$compat",
  "injectors": {
    "defaultRequire": 1
  },
  "client": [
    "client.AbstractBoatLavaMixin"
  ]
}
"@ | Set-Content (Join-Path $genR 'lavaboats-common-client.mixins.json') -Encoding UTF8
@"
{
  "required": true,
  "minVersion": "0.8",$refmap
  "package": "com.kishku7.lavaboats.$Loader.mixin",
  "compatibilityLevel": "$compat",
  "injectors": {
    "defaultRequire": 1
  },
  "client": [
    "client.$(if ($Loader -eq 'fabric') { 'BoatWaterFabricMixin' } elseif ($Loader -eq 'forge') { 'BoatFluidForgeMixin' } else { 'BoatFluidNeoForgeMixin' })"
  ]
}
"@ | Set-Content (Join-Path $genR $(if ($Loader -eq 'forge') { 'lavaboats-forge.mixins.json' } else { 'lavaboats-' + $Loader + '-client.mixins.json' })) -Encoding UTF8

# ---- 7. run cog on every marker file in gen ----
$env:PYTHONDONTWRITEBYTECODE = '1'
Get-ChildItem (Join-Path $gen 'src\main\java') -Recurse -File -Filter *.java |
    Where-Object { (Get-Content $_.FullName -Raw) -match '\[\[\[cog' } | ForEach-Object {
        & cog -r -D loader=$Loader -D ver=$McVerNum -D codegen=$cg $_.FullName | Out-Null
        if ($LASTEXITCODE -ne 0) { throw ("cog failed: " + $_.FullName) }
    }
Write-Host ("cog-gen OK: {0} (legacy={1} renamed={2} java17={3})" -f $Cell, $legacy, $renamed, $java17)
