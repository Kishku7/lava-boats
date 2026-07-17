# Lava Boats - Build Guide (minecraft-1.20-26.3 branch)

This branch is the SOLE source for Lava Boats: one codebase builds every supported Minecraft
version for every applicable loader - Fabric (plus Quilt on the pre-26 lines), Forge, and NeoForge.
For what the mod does, see the landing page: [Lava Boats](https://github.com/Kishku7/lava-boats).
Report issues at the [mod_support hub](https://github.com/Kishku7/mod_support/issues).

## What you need installed

- JDK 21 for the pre-26 cells (the 17-target lines compile with `--release 17`) and JDK 25 for the 26 cells.
- Python 3 with Cog: `pip install cogapp`.
- PowerShell 7.

## How to build

```powershell
# one cell (materialize its gen/ tree, then build it)
pwsh -File scripts\cog-gen.ps1 -Cell Fabric/1.21.8
cd Fabric\1.21.8; .\gradlew.bat build

# whole loader lines (all output -> dist/)
pwsh -File scripts\build-fabric.ps1      # every Fabric jar (pre-26 cells + the 26 line)
pwsh -File scripts\build-neoforge.ps1    # every NeoForge jar
pwsh -File scripts\build-forge.ps1       # every Forge jar
```

## Repository layout

| Directory | What it is |
|-----------|------------|
| `shared_minecraft/` | The shared Minecraft resources - textures and the mod icon - copied into every cell's generated `gen/` resources. |
| `_codegen/` | The version/loader drift brain: `compat_core.py` + `compat_{fabric,forge,neoforge}.py` (Cog emitters), `boatdata.py` + `gen_resources.py` (per-version resource generation), and `cog_sources/` (the instrumented shared business java + per-loader files that every cell is materialized from). |
| `Fabric/`, `Forge/`, `NeoForge/` | Thin per-version build cells (`<Loader>/<mc-ver>/`): era-correct gradle wiring + the per-cell manifest. Every cell - pre-26 and the `26` line alike - builds from a generated `gen/` tree materialized by `cog-gen.ps1`. |
| `scripts/` | `cog-gen.ps1` (materializes a cell's `gen/`), `build-{fabric,forge,neoforge}.ps1` (walk every cell of a loader; the `26` line is a per-line matrix inside each). All output lands in `dist/`. |

## How the code generation works

One shared business source (`_codegen/cog_sources/shared`) plus the Cog drift brain (`_codegen`)
materializes a per-cell `gen/` tree for every cell - pre-26 and the `26` line alike.
`compat_core.py` holds the version-invariant emitters, and `compat_{fabric,forge,neoforge}.py`
carry the per-loader drift. Resources are generated per version by `boatdata.py` + `gen_resources.py`,
then `cog-gen.ps1` runs Cog over the materialized markers to produce each cell's build-ready source.

By Kishku7. All Rights Reserved.
