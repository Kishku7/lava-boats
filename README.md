# Lava Boats

**Ride lava like it's water.** Lava Boats adds **Crimson and Warped boats** (regular and chest variants) that float and steer on lava exactly like ordinary boats on water. They never burn, riders never catch fire, dropped boats bob back to the surface, and Depth Strider works in lava (1.21.11+). No custom packets - a vanilla client can still join a server running it. Client + server mod.


## Branches

Source is organized by Minecraft line. Every loader+version folder is a **standalone, self-contained
build** - no Architectury, no shared `Common/`. Inside each branch the code is grouped **loader-on-top**:
`Fabric/`, `Forge/`, `NeoForge/`, each with a sub-folder per Minecraft version. `main` (this branch) is
the overview.

- [1.20.x](https://github.com/Kishku7/lava-boats/tree/1.20.x) - Minecraft 1.20.1 - 1.20.6
- [1.21.x](https://github.com/Kishku7/lava-boats/tree/1.21.x) - Minecraft 1.21 - 1.21.11
- [26.1](https://github.com/Kishku7/lava-boats/tree/26.1) - Minecraft 26.1.2
- [26.2](https://github.com/Kishku7/lava-boats/tree/26.2) - Minecraft 26.2 (pre-release)

## Supported platforms

| MC line | Fabric / Quilt | Forge | NeoForge |
| --- | --- | --- | --- |
| `1.20.x` (1.20.1 - 1.20.6)  | 1.20.1 - 1.20.6 (+ Quilt)  | 1.20.1, 1.20.5 - 1.20.6 | 1.20.1 - 1.20.6 (incl. 1.20.2 - 1.20.4) |
| `1.21.x` (1.21 - 1.21.11)   | 1.21 - 1.21.11 (+ Quilt)   | 1.21 - 1.21.8 | 1.21 - 1.21.11 |
| `26.1` (26.1.2)             | 26.1.2 (Fabric only) | - | 26.1.2 |
| `26.2` (pre-release)        | 26.2 (Fabric only) | - | 26.2 |

- **Forge** is supported through **1.21.8** (the ForgeGradle 6 ceiling - no FG7). 1.21.9+ and all of 26.x
  are Fabric + NeoForge. The 1.20.2 - 1.20.4 range is covered on NeoForge (a dedicated gap build); a Forge
  build for 1.20.2 - 1.20.4 is not yet made (tracked as future work).
- **Quilt** runs the Fabric jar on the 1.20.x / 1.21.x lines, but **not on 26.x**: Quilt retired Quilted
  Fabric API at 26.1, so the Fabric API path Lava Boats relies on is no longer provided on Quilt there.
- Fabric / Quilt builds require **Fabric API**. Every build is standalone (no Architectury).

## Using Lava Boats

Craft a **Crimson** or **Warped** boat from 5 matching nether-stem planks (chest variants: a chest + the boat); they appear in the **Tools & Utilities** creative tab. Place one on lava and ride - it floats and steers just like a boat on water.

- Boats and their riders are fully **fire-immune** while on lava; broken or dropped boats **bob back up** to the lava surface.
- **Depth Strider** works in lava on 1.21.11 and the 26.x line.
- Adds 4 boat items (crimson / warped, plain + chest). No blocks, no commands, no custom packets.

## Downloads

- Releases: https://github.com/Kishku7/lava-boats/releases
- Modrinth: https://modrinth.com/mod/lava-boats

By Kishku7. All Rights Reserved.
