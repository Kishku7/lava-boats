# Lava Boats -- Fabric 26-line cell

The `26` cell builds the `+26.1` / `+26.2` / `+26.3` Fabric jars. Like every other cell it is
cog-driven: `cog-gen.ps1` materializes its `gen/` tree before Gradle runs. The 26 line is a
per-line matrix (deps + pack formats) inside `../../scripts/build-fabric.ps1` -- always build
through it:

```powershell
pwsh -File ..\..\scripts\build-fabric.ps1 26.1 26.2 26.3
```

Cell-owned: the era-correct Fabric gradle wiring and `fabric.mod.json` (`${mcLower}` / `${mcUpper}`
templated). The shared and per-loader source is generated from `_codegen/cog_sources`.
