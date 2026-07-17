# Lava Boats -- NeoForge 26-line cell

The `26` cell builds the `+26.1` / `+26.2` NeoForge (MDG) jars; 26.3 is a NeoForge loader gap.
Like every other cell it is cog-driven: `cog-gen.ps1` materializes its `gen/` tree before Gradle
runs. The 26 line is a per-line matrix (deps + pack formats) inside `../../scripts/build-neoforge.ps1`
-- always build through it:

```powershell
pwsh -File ..\..\scripts\build-neoforge.ps1 26.1 26.2
```

Cell-owned: the era-correct NeoForge gradle wiring and `neoforge.mods.toml` (range-templated).
The shared and per-loader source is generated from `_codegen/cog_sources`.
