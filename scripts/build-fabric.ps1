# build-fabric.ps1 -- ALL Fabric builds: pre-26 cells (cog-gen -> gradle) AND the 26 line (matrix).
# Usage: pwsh -File scripts\build-fabric.ps1 [1.21.8 26.2 ...]   (no args = everything)
param([Parameter(ValueFromRemainingArguments)][string[]]$Only)
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path $PSScriptRoot -Parent
$dist = Join-Path $repoRoot 'dist'
New-Item -ItemType Directory -Force -Path $dist | Out-Null
$jdk21 = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot'
$jdk25 = 'C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot'
$prog = Join-Path $repoRoot 'scripts\_build-fabric-progress.txt'
Remove-Item $prog -ErrorAction SilentlyContinue

# ---- pre-26 cells (cog-materialized) ----
$cells = @('1.20.1','1.20.6','1.21.1','1.21.5','1.21.8','1.21.9','1.21.11')
# ---- 26 line (matrix; cell Fabric/26 srcDirs shared_minecraft directly, no cog).
#      pf = per-26.X resource pack_format (authoritative: Memory/knowledge/pack-formats.md) ----
$matrix26 = [ordered]@{
    '26.1' = @{ mc='26.1.2';          api='0.152.1+26.1.2'; loader='0.18.6'; lo='26.1-'; hi='26.2'; pf='84' }
    '26.2' = @{ mc='26.2';            api='0.152.1+26.2';   loader='0.19.3'; lo='26.2-'; hi='26.3'; pf='88' }
    '26.3' = @{ mc='26.3-snapshot-3'; api='0.154.3+26.3';   loader='0.19.3'; lo='26.3-alpha.3'; hi='26.3-alpha.4'; pf='91' }
}
if ($Only) {
    $cells = $cells | Where-Object { $Only -contains $_ }
    $keys26 = @($matrix26.Keys) | Where-Object { $Only -contains $_ }
} else {
    $keys26 = @($matrix26.Keys)
}

foreach ($v in $cells) {
    $cell = Join-Path $repoRoot ('Fabric\' + $v)
    if (-not (Test-Path $cell)) { Add-Content $prog "$v MISSING-CELL"; continue }
    Add-Content $prog "=== $v START $(Get-Date -Format HH:mm:ss) ==="
    & pwsh -NoProfile -File (Join-Path $PSScriptRoot 'cog-gen.ps1') -Cell ('Fabric/' + $v) *>> $prog
    if ($LASTEXITCODE -ne 0) { Add-Content $prog "$v COG-FAIL"; continue }
    $env:JAVA_HOME = $jdk21   # Gradle/loom need 21+; per-cell javac --release emits the right bytecode
    Push-Location $cell
    Get-ChildItem "$cell\build\libs\*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force
    & "$cell\gradlew.bat" clean build --console=plain *> "$cell\_build.log"
    $code = $LASTEXITCODE
    Pop-Location
    $jar = Get-ChildItem "$cell\build\libs\*.jar" -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch 'sources|dev' } | Select-Object -First 1
    $warn = (Select-String -Path "$cell\_build.log" -Pattern '\.java.*warning' -ErrorAction SilentlyContinue | Measure-Object).Count
    if ($code -eq 0 -and $jar) {
        Copy-Item $jar.FullName (Join-Path $dist $jar.Name) -Force
        Add-Content $prog "$v OK -> $($jar.Name) (javac warnings: $warn)"
    } else {
        Add-Content $prog "$v FAIL exit=$code (see Fabric\$v\_build.log)"
    }
}

$cell26 = Join-Path $repoRoot 'Fabric\26'
foreach ($v in $keys26) {
    $m = $matrix26[$v]
    Add-Content $prog "=== $v START $(Get-Date -Format HH:mm:ss) (mc=$($m.mc), pf=$($m.pf)) ==="
    $env:JAVA_HOME = $jdk25
    $env:PACK_FORMAT = $m.pf
    Push-Location $cell26
    Get-ChildItem "$cell26\build\libs\*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force
    & "$cell26\gradlew.bat" clean build "-Pminecraft_version=$($m.mc)" "-Pfabric_api_version=$($m.api)" `
        "-Ploader_version=$($m.loader)" "-Pmc_lower=$($m.lo)" "-Pmc_upper=$($m.hi)" "-Pmc_line=$v" `
        --console=plain *> "$cell26\_build_$v.log"
    $code = $LASTEXITCODE
    Pop-Location
    $jar = Get-ChildItem "$cell26\build\libs" -Filter 'lava-boats-*.jar' -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch 'sources' } | Sort-Object LastWriteTime | Select-Object -Last 1
    $warn = (Select-String -Path "$cell26\_build_$v.log" -Pattern '\.java.*warning' -ErrorAction SilentlyContinue | Measure-Object).Count
    if ($code -eq 0 -and $jar) {
        Copy-Item $jar.FullName (Join-Path $dist $jar.Name) -Force
        Add-Content $prog "$v OK -> $($jar.Name) (javac warnings: $warn)"
    } else {
        Add-Content $prog "$v FAIL exit=$code (see Fabric\26\_build_$v.log)"
    }
}
Add-Content $prog "ALLDONE $(Get-Date -Format HH:mm:ss)"
