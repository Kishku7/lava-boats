# Lava Boats - Minecraft 1.21.11 (source branch)

Architectury multi-loader source for MC 1.21.11. One source tree builds both loaders.

## Build (JDK 21)
- Fabric:   ./gradlew :fabric:build      -> fabric/build/libs/
- NeoForge: ./gradlew :neoforge:build    -> neoforge/build/libs/

Shipped per-loader (NOT Forgix-merged) on 1.21.11: Forgix has a mixed-mapping
relocation issue across Fabric(intermediary) + NeoForge(mojmap) on this family.
mod_version: 1.1.2+1.21.11.
