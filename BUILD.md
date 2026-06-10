# Building Lava Boats - Minecraft 1.20.4 family

Architectury source covering MC 1.20.1-1.20.4. Two compiled jars serve all four loaders:
the Fabric jar also serves Quilt; the Forge jar also serves NeoForge (1.20.1 fork point).

## Build (JDK 17)
- Fabric (serves Quilt):        ./gradlew :fabric:build        -> fabric/build/libs/
- Forge  (serves NeoForge):     cd forge-fg6 && ./gradlew build -> forge-fg6/build/libs/

## Merge into one universal jar (optional)
scripts/merge-universal.ps1 merges the Fabric + Forge jars with Forgix 2.0 and patches
the manifest. Forgix itself is NOT bundled - build it from https://github.com/PacifistMC/Forgix
(needs JDK 24) and pass its path:

  pwsh scripts/merge-universal.ps1 -Forgix <Forgix-shadow.jar> -Java24 <jdk24>\bin\java.exe

Output: merge-work/lava-boats-1.1.2+1.20.4.jar
