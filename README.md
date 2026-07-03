# Lava Boats (Minecraft 1.20.1 - 26.3, unified source)

Adds **Crimson and Warped boats** (plain + chest variants) that ride on lava exactly like
normal boats ride on water, never burn, and protect their passengers from fire.

Version: **1.4.0**

This branch (`minecraft-1.20-26.3`) is the SOLE source: one codebase builds every
supported Minecraft version from 1.20 through 26.3 for every applicable loader --
Fabric (+ Quilt on pre-26), Forge, and NeoForge.

## Features

- **Lava buoyancy + full water-speed steering** -- nether-stem boats float on lava and
  steer at water speed (client + server side).
- **Fireproof boats** -- the boats never burn or break on lava (`fireImmune` entity types).
- **Rider safety** -- any seated passenger is fully fire-immune: no damage, no ignite,
  no flame overlay.
- **Floating drops** -- broken/dropped boat items are fire-resistant (Netherite-style)
  and rise quickly to the lava surface.
- **Depth Strider in lava** (1.21.11+, 26.x) -- the enchant's movement bonus applies to
  lava travel like it does to water.

## Platform / version coverage (42 jars from one source)

| Loader | Versions |
|--------|----------|
| Fabric (+ Quilt pre-26) | 1.20 - 1.20.6, 1.21 - 1.21.11, 26.1, 26.2, 26.3 |
| Forge | 1.20.1 - 1.21.8 (FG6 ceiling; no 1.21.2 Forge release) |
| NeoForge | 1.20.1 - 1.21.11, 26.1, 26.2 (26.3 pending a NeoForge release) |

## Layout

| Directory | What it is |
|-----------|------------|
| `shared_minecraft/` | The ONE business source: shared constants, model layers, and the buoyancy / fire-immunity / item-float / depth-strider mixins, plus the 26-shaped resources. 26 cells compile it directly. |
| `_codegen/` | The version/loader drift brain: `compat_core.py` + `compat_{fabric,forge,neoforge}.py` (Cog emitters), `boatdata.py` + `gen_resources.py` (per-version resource generation), and `cog_sources/` (the instrumented shared + per-loader files that pre-26 cells are materialized from). |
| `Fabric/`, `Forge/`, `NeoForge/` | Thin per-version build cells (`<Loader>/<mc-ver>/`): era-correct gradle wiring + the per-cell manifest. Pre-26 cells build from a generated `gen/` tree; the `26` cells srcDir `shared_minecraft` directly. |
| `scripts/` | `cog-gen.ps1` (materializes a cell's `gen/`), `build-{fabric,forge,neoforge}.ps1` (pre-26 cell walkers), `build-{fabric,neoforge}-26.ps1` (26-line matrix builds). All output lands in `dist/`. |

## Build

```powershell
# one cell
pwsh -File scripts\cog-gen.ps1 -Cell Fabric/1.21.8   # pre-26 only
cd Fabric\1.21.8; .\gradlew.bat build

# whole loader lines
pwsh -File scripts\build-fabric.ps1        # pre-26 Fabric cells -> dist/
pwsh -File scripts\build-fabric-26.ps1     # 26.1 26.2 26.3 -> dist/
pwsh -File scripts\build-neoforge.ps1
pwsh -File scripts\build-neoforge-26.ps1
pwsh -File scripts\build-forge.ps1
```

Requires JDK 21 (pre-26 cells; 17-target lines compile with `--release 17`) and JDK 25
(26 cells), Python 3 with `cogapp` (`pip install cogapp`).

## Links

- Modrinth: https://modrinth.com/mod/lava-boats
- Issues: https://github.com/Kishku7/lava-boats/issues

By Kishku7. All Rights Reserved.
