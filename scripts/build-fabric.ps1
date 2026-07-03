# build-fabric.ps1 -- walk the pre-26 Fabric cells: cog-gen -> gradle build -> dist/.
# Usage: pwsh -File scripts\build-fabric.ps1 [1.21.8 ...]   (no args = all pre-26 cells)
# The 26 line builds via scripts\build-all-fabric-26.ps1 (matrix-driven, no cog) until folded in.
param([Parameter(ValueFromRemainingArguments)][string[]]$Only)
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path $PSScriptRoot -Parent
$dist = Join-Path $repoRoot 'dist'
New-Item -ItemType Directory -Force -Path $dist | Out-Null

$jdk17 = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot'
$jdk21 = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot'

$cells = @('1.20.1','1.20.6','1.21.1','1.21.5','1.21.8','1.21.9','1.21.11')
if ($Only) { $cells = $cells | Where-Object { $Only -contains $_ } }

$prog = Join-Path $repoRoot 'scripts\_build-fabric-progress.txt'
Remove-Item $prog -ErrorAction SilentlyContinue
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
    $warn = (Select-String -Path "$cell\_build.log" -Pattern '^.*\.java.*warning' -ErrorAction SilentlyContinue | Measure-Object).Count
    if ($code -eq 0 -and $jar) {
        Copy-Item $jar.FullName (Join-Path $dist $jar.Name) -Force
        Add-Content $prog "$v OK -> $($jar.Name) (javac warnings: $warn)"
    } else {
        Add-Content $prog "$v FAIL exit=$code (see Fabric\$v\_build.log)"
    }
}
Add-Content $prog "ALLDONE $(Get-Date -Format HH:mm:ss)"
