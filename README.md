# Lava Boats

Ride lava like it's water.

Lava Boats adds Crimson and Warped boats (regular and chest variants) that float and
steer on lava exactly like ordinary boats on water. They never burn, riders never catch
fire, dropped boats bob back to the surface, and Depth Strider works in lava (1.21.11+).
No custom packets - a vanilla client can still join a server running it. Client + server mod.

[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?logo=discord&logoColor=white)](https://discord.gg/2ZxzbCzAHe)

## Branches

Source is organized by Minecraft line. Every loader+version folder is a **standalone,
self-contained build** - no Architectury, no shared `Common/` folder. Inside each branch
the code is grouped **loader-on-top**: `Fabric/`, `Forge/`, `NeoForge/`, each with a
subfolder per Minecraft version. `main` (this branch) is the overview.

- [1.20.x](https://github.com/Kishku7/lava-boats/tree/1.20.x) — Minecraft 1.20.1 – 1.20.6
- [1.21.x](https://github.com/Kishku7/lava-boats/tree/1.21.x) — Minecraft 1.21 – 1.21.11
- [26.1](https://github.com/Kishku7/lava-boats/tree/26.1) — Minecraft 26.1.2
- [26.2](https://github.com/Kishku7/lava-boats/tree/26.2) — Minecraft 26.2 (pre-release)

## Supported platforms

| MC line | Fabric | Quilt | Forge | NeoForge |
| --- | :---: | :---: | :---: | :---: |
| `1.20.x` (1.20.1 – 1.20.6) | ✅ 1.20.4, 1.20.6 | ✅ | 1.20.1, 1.20.6 | 1.20.1, 1.20.4 (1.20.2–1.20.4 gap), 1.20.6 |
| `1.21.x` (1.21 – 1.21.11) | ✅ 1.21.1, 1.21.5, 1.21.8, 1.21.11 | — | 1.21.1, 1.21.5, 1.21.8 | 1.21.1, 1.21.5, 1.21.8, 1.21.11 |
| `26.1` (26.1.2) | ✅ | ✅ | — | ✅ |
| `26.2` (pre-release) | ✅ | ✅ | — | ✅ |

Quilt runs the Fabric build. Forge support ends at 1.21.8 (no Forge for 1.21.9+); the
26.x lines are Fabric + NeoForge. NeoForge covers the 1.20.2–1.20.4 gap via a dedicated
`NeoForge/1.20.4` build. Every build is **standalone (no Architectury)** and ships its own
new-style boat model. Dependencies: Fabric API (Fabric/Quilt).

## Building from source

Each loader+version folder is its own self-contained build root. Check out a branch and
build the one you want:

    # e.g. on the 1.20.x branch:
    cd Fabric/1.20.4 && ./gradlew build
    cd NeoForge/1.20.4 && ./gradlew build

No shared `common` to wire up - each folder builds independently. Each folder has a README
describing what it builds and the output jar.

## Downloads

- Releases: https://github.com/Kishku7/lava-boats/releases
- Modrinth: https://modrinth.com/mod/lava-boats

By Kishku7. All Rights Reserved.
