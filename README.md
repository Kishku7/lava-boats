# Lava Boats

Ride lava like it's water.

Lava Boats adds Crimson and Warped boats (regular and chest variants) that float and
steer on lava exactly like ordinary boats on water. They never burn, riders never catch
fire, dropped boats bob back to the surface, and Depth Strider works in lava (1.21.11+).
No custom packets - a vanilla client can still join a server running it. Client + server mod.

[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?logo=discord&logoColor=white)](https://discord.gg/2ZxzbCzAHe)

## Branches

Source is organized by Minecraft line. Inside each branch the code is grouped
**loader-on-top**: `Common/` (shared Architectury code, one folder per MC version),
then `Fabric/`, `Forge/`, `NeoForge/`, each with a subfolder per Minecraft version.
`main` (this branch) is the overview.

- [1.20.x](https://github.com/Kishku7/lava-boats/tree/1.20.x) — Minecraft 1.20.1 – 1.20.4
- [1.21.x](https://github.com/Kishku7/lava-boats/tree/1.21.x) — Minecraft 1.21.11
- [26.1](https://github.com/Kishku7/lava-boats/tree/26.1) — Minecraft 26.1.2
- [26.2](https://github.com/Kishku7/lava-boats/tree/26.2) — Minecraft 26.2 (pre-release)

## Supported platforms

| MC line | Fabric | Quilt | Forge | NeoForge |
| --- | :---: | :---: | :---: | :---: |
| `1.20.x` (1.20.1 – 1.20.4) | ✅ | ✅ | 1.20.1 | 1.20.1 (via the Forge jar) |
| `1.21.x` (1.21.11) | ✅ | — | — | ✅ |
| `26.1` (26.1.2) | ✅ | — | — | ✅ |
| `26.2` (pre-release) | ✅ | — | — | ✅ |

Quilt runs the Fabric build on 1.20.x; newer lines are Fabric + NeoForge (Quilt lacks
hashed mappings for them, classic Forge ends at 1.20.x). 26.x is standalone (no Architectury).
Dependencies: Fabric API (Fabric/Quilt); Architectury API on 1.20.x and 1.21.11.

## Building from source

Each loader+version folder is its own build root. Check out a branch and build the one you want:

    # e.g. on the 1.20.x branch:
    cd Fabric/1.20.4 && ./gradlew build
    cd Forge/1.20.4  && ./gradlew build

Architectury families pull their shared `common` from `../../Common/<version>` automatically.
Each folder has a README describing what it builds.

## Downloads

- Releases: https://github.com/Kishku7/lava-boats/releases
- Modrinth: https://modrinth.com/mod/lava-boats

By Kishku7. All Rights Reserved.
