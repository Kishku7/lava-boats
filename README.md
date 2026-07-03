# Lava Boats

**Ride lava like it's water.** Lava Boats adds **Crimson and Warped boats** (regular and chest variants) that float and steer on lava exactly like ordinary boats on water. They never burn, riders never catch fire, dropped boats bob back to the surface, and Depth Strider works in lava (1.21.11+). No custom packets - a vanilla client can still join a server running it. Client + server mod.

Current version: **1.4.1**

## Source

All source lives on ONE branch:
[`minecraft-1.20-26.3`](https://github.com/Kishku7/lava-boats/tree/minecraft-1.20-26.3) --
a single unified codebase that builds every supported Minecraft version from 1.20 through
26.3 for every applicable loader (42 jars). One shared business source + a small
version/loader drift brain; thin per-version build cells carry only the era's build wiring.
`main` (this branch) is the overview page.

The old per-line branches (`1.20.x`, `1.21.x`, `26`) are retired -- their history is
preserved in the unified branch's ancestry and the `pre-unify/*` tags.

## Supported platforms (1.4.0)

| Loader | Versions |
| --- | --- |
| Fabric (+ Quilt pre-26) | 1.20 - 1.20.6, 1.21 - 1.21.11, 26.1, 26.2, 26.3 |
| Forge | 1.20.1 - 1.21.8 (FG6 ceiling; Forge shipped no 1.20.5 or 1.21.2) |
| NeoForge | 1.20.1 - 1.21.11, 26.1, 26.2 (26.3 pending a NeoForge release) |

- **Quilt** runs the Fabric jar on the 1.20.x / 1.21.x lines, but **not on 26.x** (Quilt
  retired Quilted Fabric API at 26.1).
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
