# Merge the Fabric + Forge builds into one universal jar (Forgix 2.0) and fix the manifest.
# NeoForge 1.20.1 runs the Forge metadata; Quilt runs the Fabric metadata - one jar serves all four.
#
# Prerequisites (not bundled in this repo - provide your own):
#   - Forgix 2.0 shadow jar. Build it from https://github.com/PacifistMC/Forgix (requires JDK 24).
#   - A JDK 24 java.exe (Forgix's Manifold processor requires JDK 24 to RUN the merge).
#
# Provide both via parameters or environment variables:
#   pwsh scripts/merge-universal.ps1 -Forgix <path-to-Forgix-shadow.jar> -Java24 <path-to-jdk24>\bin\java.exe
#   (or set $env:FORGIX_JAR and $env:JAVA24_EXE)
#
# Run after :fabric:build and the forge-fg6 build have produced their per-loader jars.
param(
    [string]$Forgix = $env:FORGIX_JAR,
    [string]$Java24 = $env:JAVA24_EXE
)
$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent
$ver  = "1.1.2+1.20.4"

if (-not $Forgix -or -not (Test-Path $Forgix)) { throw "Forgix shadow jar not found. Pass -Forgix <path> or set `$env:FORGIX_JAR. See header for build instructions." }
if (-not $Java24 -or -not (Test-Path $Java24)) { throw "JDK 24 java.exe not found. Pass -Java24 <path> or set `$env:JAVA24_EXE." }

$work = Join-Path $root "merge-work"
Remove-Item $work -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $work | Out-Null

# Forgix MUTATES inputs -> feed COPIES
Copy-Item "$root\fabric\build\libs\lava-boats-$ver-fabric.jar" "$work\fabric.jar" -Force
Copy-Item "$root\forge-fg6\build\libs\lava-boats-$ver-forge.jar" "$work\forge.jar" -Force
$out = "$work\lava-boats-$ver.jar"

& $Java24 -cp $Forgix io.github.pacifistmc.forgix.Forgix mergeJars --output $out --fabric "$work\fabric.jar" --forge "$work\forge.jar"

# Fix Forgix manifest bug: it renames colliding configs to *_forge.json but misses one MixinConfigs
# reference (lavaboats-common-client.mixins.json -> *_forge.json). Repoint it to the file that exists.
$py = @"
import zipfile, os
src = r'$out'; tmp = src + '.tmp'
with zipfile.ZipFile(src,'r') as zin, zipfile.ZipFile(tmp,'w',zipfile.ZIP_DEFLATED) as zout:
    for it in zin.infolist():
        d = zin.read(it.filename)
        if it.filename == 'META-INF/MANIFEST.MF':
            d = d.decode('utf-8').replace('lavaboats-common-client.mixins.json','lavaboats-common-client.mixins_forge.json').encode('utf-8')
        zout.writestr(it, d)
os.replace(tmp, src)
print('manifest patched')
"@
$pyf = Join-Path $work "patch.py"; Set-Content -Path $pyf -Value $py -Encoding UTF8
python $pyf

Write-Host "Universal jar: $out"
