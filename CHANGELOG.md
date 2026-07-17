# Changelog

All notable changes to lava-boats are documented here. Format based on Keep a Changelog.
Lava Boats is a Fabric/Forge/NeoForge/Quilt mod adding Crimson & Warped boats (plain + chest)
that ride on lava like normal boats ride on water. Modrinth: hZpGaYjV. GitHub: Kishku7/lava-boats.

## [1.4.5] - 2026-07-16
### Fixed
- D4 dead-zone (1.21.9/1.21.10/1.21.11) textures: the previous plain-int pack_format (Option C, 1.4.3) left Crimson/Warped boats UNTEXTURED on Forge/NeoForge dead-zone clients. Doctrine-correct fix that makes both the client resource codec and server data codec work: Fabric + NeoForge ship NO pack.mcmeta (each loader synthesises the correct per-type metadata); Forge ships the exact range on the DATA major (1.21.9/1.21.10 = 88, 1.21.11 = 94). Boats now render textured on all three loaders in the dead zone (client-render eyeballed on Forge 1.21.10/1.21.11, NeoForge 1.21.10/1.21.11, Fabric 1.21.9/1.21.11).
### Changed
- MC 26.3 Fabric cell bumped 26.3-snapshot-3 -> 26.3-snapshot-4 (fabric-api 0.155.1+26.3, loader 0.19.3, dep 26.3-alpha.4, pack_format 92).
- Targeted release: only the 8 changed cells rebuilt + republished (Fabric 1.21.9/1.21.11/26.3, NeoForge 1.21.9/1.21.10/1.21.11, Forge 1.21.10/1.21.11); the other 36 jars remain at 1.4.4. All 8 build -Xlint:all with zero warnings.

## [1.4.4] - 2026-07-09
### Fixed
- Unified the MC 26 cells onto the single-source cog pipeline (D15): the 26 Fabric/NeoForge cells no longer keep hand-maintained "plain twins" of the entrypoints, ModEntities, ModItems and mixins. This fixes silent drift where the shipped 26 jars ran older entity/item code than the pre-26 jars -- the pre-26 dropItemFor/init refactor never reached the 26 twins, and check-sync.ps1 only compared 2 of ~11 twinned files so it went unnoticed.
### Changed
- Fabric creative-tab registration consolidated into one version-branched place (ModItems): itemgroup.v1 pre-26, creativetab.v1 CreativeModeTabEvents on 26 (the Fabric API renamed it). Client model-layer registrar EntityModelLayerRegistry -> ModelLayerRegistry, and recipe-unlock getPlayer() -> player on 26, both now cog-version-branched.
- Retired check-sync.ps1 (the 26 twins it guarded no longer exist; 26 is fully cog-generated like every other cell).

## [1.4.3] - 2026-07-08
### Added
- Forge cells 1.21.10 (forge 60.1.9) and 1.21.11 (forge 61.1.0); Forge ceiling raised from a false-claimed 1.21.8 to a real, end-to-end 1.21.11 (Forge now 15 cells; 1.21.9 stays gated).
- `_codegen/compat_forge.py` gained the `renamed(>=1.21.11)` axis (id_type/boat_pkg/boat_model_pkg, mirrored from NeoForge) and an `eb8` (forge 60+) branch.
- Published full 44-jar matrix to Modrinth (ADD-only): Fabric 10, NeoForge 19, Forge 15; new Forge 1.21.10 (XFwWoqXz) and 1.21.11 (fTvPE4DX); 26.3-snapshot-3 = beta.
### Changed
- Whole matrix version bumped 1.4.1/1.4.2 -> 1.4.3. All 44 jars build with -Xlint:all, zero warnings.
- D12: all 39 manifests + branch README issue links repointed from lava-boats/issues to mod_support/issues.
- Repo made PUBLIC; Issues/Projects/Wiki/Discussions turned OFF; Modrinth source_url set to the repo.
- D4 pack-format codec-split (1.21.9-1.21.11): kept plain int (Option C). Accepted cost: untextured boats on Forge/NeoForge clients for those 3 versions; servers + Fabric unaffected.
### Fixed
- Stripped 3 non-ASCII em-dashes from AbstractBoatLavaMixin and LavaDepthStriderMixin.
- New Forge 1.21.10 + 1.21.11 boot-passed CRITICAL server smoketest (lavaboats loaded, 3 mixin configs applied, clean stop).

## [1.4.2] - 2026-07-07
### Changed
- Fabric 26.3 cell bumped to MC 26.3-snapshot-3 (snapshot-3 exclusive). Retargeted mc=26.3-snapshot-3, fabric-api 0.154.3+26.3, pack_format 91, dep pinned 26.3-alpha.3. No source change.
### Added
- Modrinth 1.4.2+26.3-snapshot-3 (id OpPwxx9s, loader fabric, fabric-api required), ADD-only, beta.
### Fixed
- Smoketest on Raider (Fabric 26.3-snapshot-3 headless): client boots/renders, all mixins apply (AbstractBoat client mixin injection points held across snap-2 -> snap-3). Cell pass.

## [1.4.1] - 2026-07-03
### Changed
- Published all 42 jars to Modrinth (hZpGaYjV; release, 26.3-snapshot-2 = beta). Functionally-identical rebuild of the 59/59-boot-gated 1.4.0.
- Code-review round (commit 701cef2): build walkers consolidated to one scripts/build-<loader>.ps1 each (-26 scripts folded in); unified emit_boat_fluid_mixin + shared LAYERS/RENDER fragments in compat_core; Platform.java removed (id construction inlined via loader-aware make_id); added scripts/check-sync.ps1 drift tripwire; dead code pruned; NF/26 moved to 3-file mixins naming; MDG 2.0.141.
### Fixed
- Full 42-jar rebuild at zero warnings; check-sync caught and aligned 2 real cog-source drifts.

## [1.4.0] - 2026-07-02
### Changed
- Single-source campaign: collapsed 26 / 1.21.x / 1.20.x into ONE cross-version branch `minecraft-1.20-26.3` (M1/ChunkSmith pattern). 42 jars from one shared_minecraft + _codegen brain (Fabric 10, NeoForge 19, Forge 13), all -Xlint:all zero warnings.
- Convergence: 1.20 LavaBoat/LavaChestBoat subclasses -> BoatDropMixin; 26-Fabric WrapOperation buoyancy retired for shared AbstractBoatLavaMixin + BoatWaterFabricMixin. Architectury and Stonecutter fully removed.
- Added Fabric 26.3-snapshot-2 cell (NeoForge 26.3 = loader gap).
### Fixed
- Exhaustive boot gate green: Fabric 25/25, NeoForge 21/21, Forge 13/13 (Forge/1.20.5 = loader gap, floor-gated).
- 26-line pack_format corrected from plain-int to range form (84/88/90); 26.1 NeoForge loader floor lowered [26.1.2.0-beta,) -> [26.1.0-alpha,) so 26.1/26.1.1 servers load; classic 1.20.x tomls made honest per-version; fapi EntityRendererRegistry deprecation removed; NeoForge FluidType deprecations suppressed narrowly; Forge 51 orphan line + ctx injection map + RL factory backport at 49.2.
### Note
- Not published at 1.4.0; superseded by the 1.4.1 Modrinth publish. Old 1.20.x/1.21.x/26 branches deleted from origin (pre-unify/* rollback tags kept).

## [1.2.4 / 1.3.0] - 2026-06-22
### Changed
- Pre-unification version lines: 26.x line at 1.2.4, the 1.20.x/1.21.x lines at 1.3.0. Superseded by the 1.4.0 single-source unify; used as the diff baseline that surfaced the 1.4.0 defect fixes.
- 1.21.x branch consolidated to Cog-only (Stonecutter removed), Option A shared common module: three separate loader trees plus a sibling common/ cog-materialized per MC version (GitHub fe2788c).
### Fixed
- Legacy 1.21/1.21.1 crafting recipes corrected (recipe/ vs recipes/, flat vs object ingredient) via shared per-version resource generator.
- Validated all 25 jars build (Fabric 5, Forge 8, NeoForge 12); boot-smoketested every drift boundary. Modrinth publishing for the 1.21.x line was HELD at this stage.

## [1.2.3+26.2] - 2026-06-17
### Changed
- 26.2 STABLE release (Fabric + NeoForge), recompiled vs stable MC 26.2 / fabric-api 0.152.1+26.2 / NeoForge 26.2.0.1-beta. Modrinth + GitHub v1.2.3+26.2.
### Fixed
- NeoForge 26.2 confirmed working in-game live (2026-06-17), not just build-green.

## [1.2.2] - 2026-06-17
### Changed
- README hierarchy rebuild to the README-update spec: added platform-level (L2) READMEs on every branch, corrected stale per-version MC ranges, dropped the hardcoded mod_version in favor of a glob, fully-qualified all cross-folder links.
- Quilt Option A applied: 26.x = Fabric + NeoForge only (QFAPI retired at 26.1); Quilt kept on 1.20.x/1.21.x. Dropped `quilt` from the 5 26.x Fabric Modrinth versions (loaders=[fabric]).

## [1.2.0] - 2026-06-16
### Added
- De-Architectury full-coverage rollout: 22 jars (18 new standalone + 4 26.x rebuilt at 1.2.0). Fully standalone per-loader builds (no Common) - the model going forward.
- Coverage: Fabric/Quilt 1.20.1 -> 26.2; Forge 1.20.1 -> 1.21.8; NeoForge 1.20.1 -> 1.21.11 (incl the 1.20.2-1.20.4 gap) + 26.x.
- Modrinth: 22 versions (env=client_and_server; 8 fabric+quilt, 5 forge, 9 neoforge incl 26.2 beta). GitHub release v1.2.0 (20 stable assets; 26.2 excluded per pre-release rule). Branches restructured to standalone (no Common).
### Fixed
- Every jar smoke-tested on a real server (boot + boats summon + mixins apply). Caveat: boat-on-lava client rendering not manually verified in-game.

## [1.1.4+26.2-rc-2] - 2026-06-14
### Added
- 26.2-rc-2 pre-release: Fabric 1.1.4 (Modrinth yYGGlQ9c, fabric+quilt) + NeoForge 1.1.4 (zz42p26H). Modrinth beta only (no GitHub release per pre-release rule). MC 26.2-rc-2, loader 0.19.3, fapi 0.152.0+26.2.

## [1.1.2+26.2] - 2026-06-10
### Added
- Fabric-only ALPHA for the 26.2 pre-release line (game_versions 26.2-pre-1..pre-6). GitHub release v1.1.2+26.2; Modrinth version kNOinxjc (type alpha, loader fabric). NeoForge 26.2 pending upstream NeoForge release.

## [1.1.2] - 2026-06-10
### Fixed
- Server-side buoyancy: moved AbstractBoatLavaMixin from client-only to common. Root cause of multiplayer sinking - buoyancy was client-only, so on a passenger-handshake hiccup the server (no buoyancy) controlled the boat and it sank.
- Bob-up resurfacing: floatBoat TAIL @Inject drives a firm 0.10/tick rise while the eye is submerged in lava (vanilla UNDER_WATER buoyancy 0.01 was too weak to recover from a ghast knock).
- Depth Strider in lava (1.21.11 + 26.1.2 only): LavaDepthStriderMixin honours WATER_MOVEMENT_EFFICIENCY in travelInLava. 1.20.1 ships boat fixes only (lacks travelInLava + the attribute).
### Changed
- Published all families (parity GitHub <-> Modrinth). GitHub v1.1.2+1.20.4 (merged), +1.21.11, +26.1.2 (main, commit 458a3d9). Modrinth 1.1.2+1.20.4 (33eJfx2V), +1.21.11 (CdSEpFKZ), +26.1.2 (L5r6Zu8A).
- 1.20.4 shipped as a Forgix universal merged jar; 1.21.11 + 26.x stay per-loader (Forgix mixed-mapping relocation bug).
- GitHub branch model locked: every distinct release = its own branch named by MC version (main, 26.1.2, 1.21.11, 1.20.4); old arch/<ver> naming retired. main slimmed to a landing page; history scrubbed via filter-repo (2026-06-10).
### Removed
- Old v1.1.1 GitHub releases and tags deleted (superseded). Better Boat Movement (bbm) and moveboats decompiled and cleared as buoyancy conflicts - no compat mixin and no bbm PR needed.

## [1.1.1] - 2026-06-09
### Added
- First multi-loader release: Fabric + Quilt + Forge + NeoForge across MC 1.20.x, 1.21.11, and 26.1.2. Architectury restructure (common/fabric/forge); Quilt runs the Fabric jar, NeoForge 1.20.1 runs the Forge jar; Forgix-merged universal jar for the 1.20.x family.
- GitHub releases v1.1.1+1.20.4 (merged universal), +1.21.11 (fabric + neoforge), +26.1.2 (fabric + neoforge). README rewritten with full support matrix.
- Modrinth: 1.1.1+1.20.4 (V8OI5eYs, fabric/quilt/forge/neoforge, gv 1.20.1-1.20.4), +1.21.11 (PxdTYoNW, fabric/neoforge), +26.1.2 (ZdGBE95V, fabric/neoforge). All PATCHed to env client_and_server.
### Note
- Later superseded and deleted at 1.1.2 (GitHub releases/tags only; Modrinth 1.1.1 versions left intact per never-delete rule).

## [1.0.0] - 2026-06-08
### Added
- Initial release. Crimson & Warped boats plus chest variants (crimson_boat, warped_boat, crimson_chest_boat, warped_chest_boat) that float and steer on lava at water speed. Fabric, MC 26.1.2, depends Fabric API.
- Four features: lava buoyancy + full water-speed (client-authoritative), fireproof boats (.fireImmune() entity types), rider fire-immunity for any seated passenger, and floating fireResistant boat-item drops that rise to the lava surface.
- Recipes (boat = 5 matching planks; chest boat = chest + boat), birch-based recoloured art (crimson/warped palettes, vanilla chests), mod icon.
- Published to GitHub (Kishku7/lava-boats, public) and submitted to Modrinth (hZpGaYjV, slug lava-boats, License ARR).
### Development
- Iterated through beta.3 (rider switched to fireImmune while seated; float boost stabilised to +0.20, no bounce) and beta.4 (dropped boat items rise to surface) before the 1.0.0 release. Initial +0.42 float bounced; +0.20 keeps buoyancy engaged.


