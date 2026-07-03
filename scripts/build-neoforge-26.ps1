# build-neoforge-26.ps1 -- Lava Boats 26-line NeoForge builds (matrix-driven, no cog; cell = NeoForge/26).
# Usage: pwsh -File scripts\build-neoforge-26.ps1 [26.1 26.2]     (no 26.3 NeoForge yet -- loader gap)
param([Parameter(ValueFromRemainingArguments)][string[]]$Versions)
$ErrorActionPreference = "Stop"
$repoRoot = Split-Path $PSScriptRoot -Parent
$cell = Join-Path $repoRoot "NeoForge\26"
$dist = Join-Path $repoRoot "dist"
New-Item -ItemType Directory -Force -Path $dist | Out-Null

# pf = per-26.X resource pack_format (authoritative: Memory/knowledge/pack-formats.md; range form via ${packFormat})
$matrix = [ordered]@{
    "26.1" = @{ mc="26.1.2"; neo="26.1.2.30-beta"; mcRange="[26.1,26.2)"; neoRange="[26.1.0-alpha,)"; pf="84" }
    "26.2" = @{ mc="26.2";   neo="26.2.0.1-beta";  mcRange="[26.2,26.3)"; neoRange="[26.2.0-alpha,)";  pf="88" }
}
if (-not $Versions -or $Versions.Count -eq 0) { $Versions = @($matrix.Keys) }

foreach ($v in $Versions) {
    $m = $matrix[$v]; if (-not $m) { throw "Unknown $v" }
    Write-Host "=== LB NeoForge $v (mc=$($m.mc), neo=$($m.neo), pf=$($m.pf)) ==="
    $env:PACK_FORMAT = $m.pf
    Push-Location $cell
    Get-ChildItem "$cell\build\libs\*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force
    & "$cell\gradlew.bat" clean build "-Pminecraft_version=$($m.mc)" "-Pneo_version=$($m.neo)" `
        "-Pmc_range=$($m.mcRange)" "-Pneoforge_range=$($m.neoRange)" "-Pmc_line=$v" `
        --console=plain *> "$cell\_build_$v.log"
    $rc = $LASTEXITCODE
    Pop-Location
    $jar = Get-ChildItem "$cell\build\libs" -Filter "lava-boats-*.jar" -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch 'sources' } | Sort-Object LastWriteTime | Select-Object -Last 1
    $warn = (Select-String -Path "$cell\_build_$v.log" -Pattern '\.java.*warning' -ErrorAction SilentlyContinue | Measure-Object).Count
    if ($rc -ne 0 -or -not $jar) { throw "NeoForge 26-line FAILED $v (see NeoForge\26\_build_$v.log)" }
    Copy-Item $jar.FullName (Join-Path $dist $jar.Name) -Force
    Write-Host "  -> $($jar.Name) (javac warnings: $warn)"
}
Write-Host "NeoForge 26-line builds complete."
