# Lava Boats - branch `26.2`

Source for the Minecraft **26.2 (pre-release)** line. Every loader+version folder is a **standalone, self-contained
build** - no Architectury, no shared `Common/`. Client + server mod.

> **Pre-release line.** Modrinth **beta** only; no GitHub release until 26.2 is stable. The NeoForge build targets a local NeoForge 26.2 alpha (no public NeoForge 26.2 yet).

## Platforms

- [`Fabric/`](Fabric) - 1 build(s); see its README for versions and exclusions.
- [`NeoForge/`](NeoForge) - 1 build(s); see its README for versions and exclusions.

## Not supported on this line

- **Forge** is not built for the 26.x line - ForgeGradle 6 cannot build unobfuscated Minecraft 26.x and there is no FG7.
- **Quilt** is not supported on the 26.x line - Quilt retired Quilted Fabric API at 26.1, so the Fabric API path Lava Boats uses on Fabric is no longer provided on Quilt for 26.x. (Quilt remains supported on the 1.20.x and 1.21.x branches.)

## Build

```
cd <Loader>/<version>
./gradlew build      # Windows: .\gradlew.bat build
```

Output: `build/libs/lava-boats-*.jar`. Requires JDK 25 (Minecraft 26.x toolchain).

## Links

- Other branches: [`1.20.x`](https://github.com/Kishku7/lava-boats/tree/1.20.x), [`1.21.x`](https://github.com/Kishku7/lava-boats/tree/1.21.x), [`26.1`](https://github.com/Kishku7/lava-boats/tree/26.1)
- Overview: [`main`](https://github.com/Kishku7/lava-boats/tree/main)
- Modrinth: https://modrinth.com/mod/lava-boats
- Releases: https://github.com/Kishku7/lava-boats/releases

By Kishku7. All Rights Reserved.
