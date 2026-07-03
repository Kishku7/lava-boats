# Lava Boats -- Fabric 26-line cell

Builds the `+26.1` / `+26.2` / `+26.3` Fabric jars directly from `../../shared_minecraft`
(no cog; the 26 cells never run the preprocessor). Matrix + per-line deps and pack formats
live in `../../scripts/build-fabric-26.ps1` -- always build through it:

```powershell
pwsh -File ..\..\scripts\build-fabric-26.ps1 26.1 26.2 26.3
```

Cell-owned: the fabric entrypoints (`fabric/`), the 26 registration seams
(`ModEntities`/`ModItems`), `BoatWaterFabricMixin` (plain twin of the cog source --
keep in sync with `_codegen/cog_sources/fabric`), `fabric.mod.json` (`${mcLower}`/
`${mcUpper}` templated) and the mixins jsons.
