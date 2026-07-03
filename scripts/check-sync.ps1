# check-sync.ps1 -- drift tripwire between the cog sources and their PLAIN 26-cell twins.
# The 26 cells never run cog, so shared_minecraft + the 26 loader cells keep plain copies of
# files that also exist as cog sources. This materializes each cog source at 26.1 and compares
# CODE (comments/blank/package lines ignored) against the plain twin. Exit 1 on drift.
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path $PSScriptRoot -Parent
$cg = Join-Path $repoRoot '_codegen'
$tmp = Join-Path $env:TEMP ('lb-checksync-' + [guid]::NewGuid().ToString('N').Substring(0,8))
New-Item -ItemType Directory -Force -Path $tmp | Out-Null

function Normalize($path) {
    $out = New-Object System.Collections.Generic.List[string]
    $inBlock = $false
    foreach ($ln in (Get-Content $path)) {
        $t = $ln.Trim()
        if ($inBlock) { if ($t -match '\*/') { $inBlock = $false }; continue }
        if ($t -match '^/\*' ) { if ($t -notmatch '\*/') { $inBlock = $true }; continue }
        if ($t -eq '' -or $t.StartsWith('//') -or $t.StartsWith('*') -or $t.StartsWith('package ')) { continue }
        $out.Add($t)
    }
    return $out
}

$pairs = @(
    @{ src="$cg\cog_sources\shared\LavaBoats.java";                          loader='neoforge'; plain="$repoRoot\shared_minecraft\src\main\java\com\kishku7\lavaboats\LavaBoats.java" },
    @{ src="$cg\cog_sources\shared\client\LavaBoatLayers.java";              loader='neoforge'; plain="$repoRoot\shared_minecraft\src\main\java\com\kishku7\lavaboats\client\LavaBoatLayers.java" },
    @{ src="$cg\cog_sources\shared\mixin\client\AbstractBoatLavaMixin.java"; loader='neoforge'; plain="$repoRoot\shared_minecraft\src\main\java\com\kishku7\lavaboats\mixin\client\AbstractBoatLavaMixin.java" },
    @{ src="$cg\cog_sources\fabric\fabric\mixin\client\BoatWaterFabricMixin.java";      loader='fabric';   plain="$repoRoot\Fabric\26\src\main\java\com\kishku7\lavaboats\fabric\mixin\client\BoatWaterFabricMixin.java" },
    @{ src="$cg\cog_sources\neoforge\neoforge\mixin\client\BoatFluidNeoForgeMixin.java"; loader='neoforge'; plain="$repoRoot\NeoForge\26\src\main\java\com\kishku7\lavaboats\neoforge\mixin\client\BoatFluidNeoForgeMixin.java" }
)

$fail = 0
foreach ($p in $pairs) {
    $name = Split-Path $p.src -Leaf
    $plain = $p.plain
    if (-not (Test-Path $plain)) {
        # NF/26 keeps BoatFluidNeoForgeMixin in the root mixin package (pre-taxonomy layout)
        $alt = "$repoRoot\NeoForge\26\src\main\java\com\kishku7\lavaboats\mixin\client\$name"
        if (Test-Path $alt) { $plain = $alt } else { Write-Host "MISSING plain twin: $name"; $fail++; continue }
    }
    $work = Join-Path $tmp $name
    Copy-Item $p.src $work -Force
    & cog -r -D ("loader=" + $p.loader) -D ver=26.1 -D codegen=$cg $work | Out-Null
    if ($LASTEXITCODE -ne 0) { Write-Host "COG FAIL: $name"; $fail++; continue }
    $a = Normalize $work
    $b = Normalize $plain
    $diff = Compare-Object $a $b
    if ($diff) {
        Write-Host "DRIFT: $name ($($diff.Count) differing code lines)"
        $diff | Select-Object -First 6 | ForEach-Object { Write-Host ("  {0} {1}" -f $_.SideIndicator, $_.InputObject) }
        $fail++
    } else {
        Write-Host "OK: $name"
    }
}
Remove-Item $tmp -Recurse -Force
if ($fail -gt 0) { Write-Host "check-sync: $fail file(s) drifted"; exit 1 }
Write-Host 'check-sync: all twins in sync'
