# Lava Boats

[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?logo=discord&logoColor=white)](https://discord.gg/NVcgJJRsx)

Ride lava like it's water.

Lava Boats adds Crimson and Warped boats (regular and chest variants) that float and
steer on lava exactly like ordinary boats on water. They never burn, riders never catch
fire, dropped boats bob back to the surface, and Depth Strider works in lava (1.21.11+).
No custom packets - a vanilla client can still join a server running it.

## Supported platforms

Source for each Minecraft version lives on its own branch, named for the version.
`main` (this branch) is just the overview.

| Branch    | Minecraft       | Fabric | Quilt | Forge | NeoForge |
| ---       | ---             | :---:  | :---: | :---: | :---:    |
| `1.20.4`  | 1.20.1 - 1.20.4 | Yes    | Yes   | Yes   | Yes      |
| `1.21.11` | 1.21.11         | Yes    | -     | -     | Yes      |
| `26.1.2`  | 26.1.2          | Yes    | -     | -     | Yes      |

On 1.20.1 Quilt runs the Fabric build and one universal jar serves all four loaders.
Newer families are Fabric + NeoForge (Quilt lacks mappings, classic Forge ends at 1.20.x).
Dependencies: Fabric API (Fabric/Quilt); Architectury API on 1.20.1 and 1.21.11; none on NeoForge 26.1.2.

## Building from source

Check out the branch for your Minecraft version, then:

    ./gradlew build                # 1.20.4 and 1.21.11 are multi-loader source trees
    # 26.1.2 branch: cd fabric && ./gradlew build   (and cd neoforge && ./gradlew build)

The 1.20.4 branch includes scripts/merge-universal.ps1 + BUILD.md for the universal jar.

## Downloads

- Releases: https://github.com/Kishku7/lava-boats/releases
- Modrinth: https://modrinth.com/mod/lava-boats

By Kishku7. All Rights Reserved.
