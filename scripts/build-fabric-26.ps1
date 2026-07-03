# build-fabric-26.ps1 -- Lava Boats 26-line Fabric builds (matrix-driven, no cog; cell = Fabric/26).
# Usage: pwsh -File scripts\build-fabric-26.ps1 [26.1 26.2 26.3]
param([Parameter(ValueFromRemainingArguments)][string[]]$Versions)
$ErrorActionPreference = "Stop"
$repoRoot = Split-Path $PSScriptRoot -Parent
$cell = Join-Path $repoRoot "Fabric\26"
$dist = Join-Path $repoRoot "dist"
New-Item -ItemType Directory -Force -Path $dist | Out-Null
if (-not $env:JAVA_HOME) { $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot" }

# pf = per-26.X resource pack_format (authoritative: Memory/knowledge/pack-formats.md; range form via ${packFormat})
$matrix = [ordered]@{
    "26.1" = @{ mc="26.1.2";          api="0.152.1+26.1.2"; loader="0.18.6"; lo="26.1-"; hi="26.2"; pf="84" }
    "26.2" = @{ mc="26.2";            api="0.152.1+26.2";   loader="0.19.3"; lo="26.2-"; hi="26.3"; pf="88" }
    "26.3" = @{ mc="26.3-snapshot-2"; api="0.153.2+26.3";   loader="0.19.3"; lo="26.3-"; hi="26.4"; pf="90" }
}
if (-not $Versions -or $Versions.Count -eq 0) { $Versions = @($matrix.Keys) }

foreach ($v in $Versions) {
    $m = $matrix[$v]; if (-not $m) { throw "Unknown $v" }
    Write-Host "=== LB Fabric $v (mc=$($m.mc), pf=$($m.pf)) ==="
    $env:PACK_FORMAT = $m.pf
    Push-Location $cell
    Get-ChildItem "$cell\build\libs\*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force
    & "$cell\gradlew.bat" clean build "-Pminecraft_version=$($m.mc)" "-Pfabric_api_version=$($m.api)" `
        "-Ploader_version=$($m.loader)" "-Pmc_lower=$($m.lo)" "-Pmc_upper=$($m.hi)" "-Pmc_line=$v" `
        --console=plain *> "$cell\_build_$v.log"
    $rc = $LASTEXITCODE
    Pop-Location
    $jar = Get-ChildItem "$cell\build\libs" -Filter "lava-boats-*.jar" -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch 'sources' } | Sort-Object LastWriteTime | Select-Object -Last 1
    $warn = (Select-String -Path "$cell\_build_$v.log" -Pattern '\.java.*warning' -ErrorAction SilentlyContinue | Measure-Object).Count
    if ($rc -ne 0 -or -not $jar) { throw "Fabric 26-line FAILED $v (see Fabric\26\_build_$v.log)" }
    Copy-Item $jar.FullName (Join-Path $dist $jar.Name) -Force
    Write-Host "  -> $($jar.Name) (javac warnings: $warn)"
}
Write-Host "Fabric 26-line builds complete."
