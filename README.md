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
# one pre-26 cell
pwsh -File scripts\cog-gen.ps1 -Cell Fabric/1.21.8
cd Fabric\1.21.8; .\gradlew.bat build

# whole loader lines (all output -> dist/)
pwsh -File scripts\build-fabric.ps1      # every Fabric jar (pre-26 cells + the 26 line)
pwsh -File scripts\build-neoforge.ps1    # every NeoForge jar
pwsh -File scripts\build-forge.ps1       # every Forge jar
pwsh -File scripts\check-sync.ps1        # drift tripwire: cog sources vs plain 26-cell twins
```

## Repository layout

| Directory | What it is |
|-----------|------------|
| `shared_minecraft/` | The ONE business source: shared constants, model layers, and the buoyancy / fire-immunity / item-float / depth-strider mixins, plus the 26-shaped resources. The 26 cells compile it directly. |
| `_codegen/` | The version/loader drift brain: `compat_core.py` + `compat_{fabric,forge,neoforge}.py` (Cog emitters), `boatdata.py` + `gen_resources.py` (per-version resource generation), and `cog_sources/` (the instrumented shared + per-loader files that pre-26 cells are materialized from). |
| `Fabric/`, `Forge/`, `NeoForge/` | Thin per-version build cells (`<Loader>/<mc-ver>/`): era-correct gradle wiring + the per-cell manifest. Pre-26 cells build from a generated `gen/` tree; the `26` cells srcDir `shared_minecraft` directly. |
| `scripts/` | `cog-gen.ps1` (materializes a cell's `gen/`), `build-{fabric,forge,neoforge}.ps1` (pre-26 cell walkers), `build-{fabric,neoforge}-26.ps1` (26-line matrix builds), `check-sync.ps1` (drift tripwire). All output lands in `dist/`. |

## How the code generation works

One shared business source (`shared_minecraft`) plus the Cog drift brain (`_codegen`) materializes a
per-cell `gen/` tree for each pre-26 cell; the 26 cells srcDir the shared source directly.
`compat_core.py` holds the version-invariant emitters, and `compat_{fabric,forge,neoforge}.py` carry the
per-loader drift. Resources are generated per version by `boatdata.py` + `gen_resources.py`.
`check-sync.ps1` is the tripwire that flags cog-source drift against the plain 26-cell twins.

By Kishku7. All Rights Reserved.
