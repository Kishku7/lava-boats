# Lava Boats -- unified cross-version source (branch minecraft-1.20-26.3)

Target: ONE source, MC 1.20.1 - 26.3, version 1.4.0 everywhere. M1/ChunkSmith pattern,
mod_template shape (no Plugin/shared_bukkit -- LB has no plugin). Modrinth publish is a
separate, held step. Old branches (1.20.x / 1.21.x / origin 26.1+26.2) are deleted only
after their line passes the exhaustive smoketest gate (decision 2026-07-02).

## Cell matrix (mirrors published 1.3.0 coverage + adds Fabric 26.3-snapshot)

Fabric (8 cells):
  1.20.1 (claims 1.20.1-1.20.4, JDK17)   1.20.6 (1.20.5-1.20.6, JDK21)
  1.21.1 (1.21-1.21.1)                  1.21.5 (1.21.2-1.21.5)
  1.21.8 (1.21.6-1.21.8)                  1.21.9 (1.21.9-1.21.10)
  1.21.11 (1.21.11)                       26 (line-keyed +26.1/+26.2/+26.3, snapshot-2)

Forge (13 cells, FG6, no 1.21.2 target, ceiling 1.21.8):
  1.20.1 1.20.2 1.20.3 1.20.4 1.20.6 (JDK17 thru 1.20.4; 21 from 1.20.6)
  1.21 1.21.1 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8

NeoForge (18 cells, MDG; NeoGradle/SRG at 1.20.1-1.20.4):
  1.20.1 1.20.2 1.20.3 1.20.4 1.20.6
  1.21 1.21.1 1.21.2 1.21.3 1.21.4 1.21.5 1.21.6 1.21.7 1.21.8 1.21.9 1.21.10 1.21.11
  26 (line-keyed +26.1/+26.2; 26.3 = loader gap)

## Era model (compat.py axes)

legacy (boat subclass era, <1.21.2): BoatDropMixin (replaces LavaBoat/LavaChestBoat
  subclasses -- 1.20 subclasses ONLY override getDropItem, converges same as 1.21.1 did),
  LavaBoatItem placer, custom LavaBoatRenderer. Sub-axes:
    <1.20.5 : `new ResourceLocation(ns,path)` ctor (no fromNamespaceAndPath), JDK17, pre-components
    <1.20.3 : awardRecipesByKey takes ARRAY (>=1.20.3 Collection) -- the old S1 jar claimed
              1.20.1-1.20.4 with the >=1.20.3 form; RE-VERIFY this claim gate at bring-up
modern (1.21.2-1.21.10): AbstractBoat target, BoatItem+setId, vanilla BoatRenderer+LavaBoatLayers
modern11 (>=1.21.11 incl 26): Identifier rename, vehicle.boat package move, BoatModel package
  move, LavaDepthStriderMixin present (travelInLava + WATER_MOVEMENT_EFFICIENCY)
26 axis (major>=26): Fabric API renames (ModelLayerRegistry not EntityModelLayerRegistry,
  CreativeModeTabEvents not ItemGroupEvents), loom compileOnly/localRuntime (no modImplementation),
  pack.mcmeta RANGE form (pack_format=min=max, >81 codec rule), NeoForge FMLLoader.getCurrent()
  (>=1.21.9 axis)

## Design decisions (locked)

- ONE _codegen brain (compat.py keyed loader+mcver) = merge of 1.21.x common/compat_core.py +
  the 3 per-loader _codegen/compat.py, extended down to 1.20.1 and up to 26.
- Buoyancy design everywhere = the converged 1.21.x split: shared mixin/client/AbstractBoatLavaMixin
  (Entity-cast, no MixinExtras) + per-loader fluid mixin (BoatWaterFabricMixin / BoatFluidForgeMixin /
  BoatFluidNeoForgeMixin). The 26-Fabric WrapOperation design is RETIRED at 1.4.0.
- shared_minecraft keeps plain 26-shaped copies + 26 resources; 26 cells srcDir it DIRECTLY
  (compat.py cannot affect 26). Pre-26 cells build from gen/ via scripts/cog-gen.ps1.
- Pre-26 resources are GENERATED per version (boatdata.py profile: recipe dir/schema, items/
  overrides, pack_format) + textures copied. 26 resources live in shared_minecraft.
- Cell granularity mirrors the published jar segments (no coverage change), except 26.3 added.
- Stonecutter dies with the old 1.20.x branch (last live instance).
- Smoketests only until the whole matrix is done; in-world testing after, with dedicated test
  worlds + M1. Old branch deleted per line as its smoketests pass.

## Status

- [x] Stage 0: pre-unify tags, branch, worktree, skeleton dirs
- [x] Stage 1: _codegen brain + cog_sources + shared_minecraft convergence (merged brains + shared_legacy consolidation; 1.20 axes: RL ctor @<1.21, awardRecipesByKey array @<1.20.3, result item->id @1.20.5, renderToBuffer floats @<1.21; fapi EntityRenderers @>=1.21.9)
- [x] Stage 2: ALL 42 JARS GREEN at 1.4.0, zero warnings (Fabric 10, NeoForge 19, Forge 13); era representatives boot-verified on all three loaders. Runtime-caught fixes: Forge 51 orphan line (no ctor injection -- classic .get() exactly there); Forge 49.2 RL-factory backport (loader-aware make_id); ctx injection 1.20.4+ except 1.21; Mixin-0.8.5 JAVA_17 level on forge/neoforge <1.21.2; 26 pack.mcmeta range form
- [ ] Stage 3: exhaustive smoketest gate IN PROGRESS (temp\lb-gate-results.txt) + per-line old-branch retirement
- [ ] Stage 4: cleanup, READMEs, push; publish HELD
