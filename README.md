# Lava Boats

A Fabric mod for Minecraft 26.1.2 that adds **Crimson** and **Warped** boats
(plain and chest variants) which ride on lava exactly like normal boats ride on water.

## Features
1. **Lava buoyancy + full speed** - nether-stem boats float on the lava surface and
   move at the same speed they would on water. (Client-side physics.)
2. **Fireproof boats** - the boats never burn or break in lava. (Server-side; the
   entity types are registered fire-immune.)
3. **Rider safety** - any living passenger of a lava boat takes no fire damage and is
   extinguished on board. (Server-side.)

## Client-optional
One jar, `environment: "*"`. Install on the server for the protection features
(they apply for everyone). Install on clients too for the actual lava-riding buoyancy.
A vanilla client can still connect; it just won't get the float (boats sink, as normal).
No new networking, so no forced handshake.

## Recipes
- Boat: 5 matching planks in the standard boat shape.
- Chest boat: chest + the boat (shapeless).

## Build
`gradlew build` -> `build/libs/lava-boats-<version>.jar`

## Art
Boat textures are recoloured from the RB_128x birch boat art to match the crimson /
warped plank palettes, including PBR normal/specular maps. See `tools/recolor_boats.py`.
