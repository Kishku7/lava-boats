# Lava Boats -- NeoForge 26-line cell

Builds the `+26.1` / `+26.2` NeoForge jars (MDG) directly from `../../shared_minecraft`
(no cog; the 26 cells never run the preprocessor). 26.3 = NeoForge loader gap. Matrix +
per-line deps and pack formats live in `../../scripts/build-neoforge-26.ps1` -- always
build through it:

```powershell
pwsh -File ..\..\scripts\build-neoforge-26.ps1
```

Cell-owned: the neoforge entrypoints, the 26 registration seams (`ModEntities`/`ModItems`
DeferredRegister), `BoatFluidNeoForgeMixin`, `neoforge.mods.toml` (range-templated) and
the mixins jsons.
