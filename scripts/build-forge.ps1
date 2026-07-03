# build-forge.ps1 -- walk the pre-26 Forge cells: cog-gen -> gradle build -> dist/.
# Usage: pwsh -File scripts\build-forge.ps1 [1.21.8 ...]   (no args = all pre-26 cells)
# Forge has no 26 line (FG6 ceiling 1.21.8).
param([Parameter(ValueFromRemainingArguments)][string[]]$Only)
$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path $PSScriptRoot -Parent
$dist = Join-Path $repoRoot 'dist'
New-Item -ItemType Directory -Force -Path $dist | Out-Null

$cells = @('1.20.1','1.20.2','1.20.3','1.20.4','1.20.6',
           '1.21','1.21.1','1.21.3','1.21.4','1.21.5','1.21.6','1.21.7','1.21.8')
if ($Only) { $cells = $cells | Where-Object { $Only -contains $_ } }

$prog = Join-Path $repoRoot 'scripts\_build-forge-progress.txt'
Remove-Item $prog -ErrorAction SilentlyContinue
foreach ($v in $cells) {
    $cell = Join-Path $repoRoot ('Forge\' + $v)
    if (-not (Test-Path $cell)) { Add-Content $prog "$v MISSING-CELL"; continue }
    Add-Content $prog "=== $v START $(Get-Date -Format HH:mm:ss) ==="
    & pwsh -NoProfile -File (Join-Path $PSScriptRoot 'cog-gen.ps1') -Cell ('Forge/' + $v) @srcArgs *>> $prog
    if ($LASTEXITCODE -ne 0) { Add-Content $prog "$v COG-FAIL"; continue }
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
        Add-Content $prog "$v FAIL exit=$code (see Forge\$v\_build.log)"
    }
}
Add-Content $prog "ALLDONE $(Get-Date -Format HH:mm:ss)"
