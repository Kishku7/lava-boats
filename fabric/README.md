# Lava Boats

Ride lava like it's water.

**Lava Boats** adds **Crimson** and **Warped** boats - in both regular and chest variants - that
float and steer on lava exactly the way ordinary boats do on water. The boats never burn, their
passengers never catch fire, and a dropped boat bobs back up to the lava surface instead of sinking.

Available across **four mod loaders and three Minecraft versions** - Fabric, Quilt, Forge, and
NeoForge, on Minecraft 1.20.1, 1.21.11, and 26.1.2.

## Supported platforms

| Minecraft | Fabric | Quilt | Forge | NeoForge |
| --- | :---: | :---: | :---: | :---: |
| **1.20.1** | Yes | Yes | Yes | Yes |
| **1.21.11** | Yes | - | - | Yes |
| **26.1.2** | Yes | - | - | Yes |

One source, every modern loader. On 1.20.1 the family covers 1.20-1.20.4, and Quilt runs the Fabric
build. On the newer versions Mojang's engine narrows the field to Fabric + NeoForge (Quilt has no
mappings for them yet, and classic Forge ends at 1.20.x). Grab the jar that matches your loader and
Minecraft version from the [Releases](https://github.com/Kishku7/lava-boats/releases) page or
[Modrinth](https://modrinth.com/mod/lava-boats).

## Features

- **Four new boats** - Crimson and Warped, each with a chest variant - crafted from their matching
  nether-stem planks just like vanilla boats (5 planks for a boat; chest + boat for a chest boat).
- **Lava travel** - nether-stem boats float on the lava surface and move at full water speed. Place
  one on lava and go.
- **Depth Strider in lava** - Depth Strider boots carry over to lava, speeding you through it just
  like water and scaling with the enchantment level. (Minecraft 1.21.11 and 26.1.2.)
- **Fireproof** - the boats are immune to fire and lava; they won't burn or break.
- **Safe passengers** - anyone riding a lava boat takes no fire or lava damage and shows no flames.
- **Always resurfaces** - knock a lava boat under (a ghast fireball, an explosion) and it drives
  firmly back up to the surface instead of sinking to the floor.
- **Floating drops** - break a boat over lava (or drop one in) and the item rises straight back to
  the surface, fire-resistant like Netherite.
- **Recipes unlocked on join** - the boat recipes appear in your recipe book the moment you enter a
  world, so they're easy to find.
- **High-quality art** - 128x boat textures with PBR (normal + specular) maps, recoloured to match
  the Crimson and Warped plank palettes. The chest on a chest boat still looks like a chest.

## How it works

Lava buoyancy runs on both the server and the client, so a lava boat floats whether it's being
ridden, sitting empty, or knocked around - the boat's fluid checks are taught to treat lava like
water for nether-stem boats, and vanilla's own float/steer logic does the rest. The fireproofing and
passenger safety protect everyone, even players without the mod (they just won't get the lava
buoyancy themselves). No new networking and no custom packets, so a vanilla client can still connect
to a server running Lava Boats.

## Installation

Drop the jar that matches your loader and Minecraft version into your `mods/` folder, along with its
dependencies:

| Platform | Required dependencies |
| --- | --- |
| Fabric / Quilt | [Fabric API](https://modrinth.com/mod/fabric-api) |
| Fabric / NeoForge on 1.20.1 and 1.21.11 | [Architectury API](https://modrinth.com/mod/architectury-api) |
| Forge 1.20.1 | [Architectury API](https://modrinth.com/mod/architectury-api) |
| NeoForge 26.1.2 | (none) |

Works in singleplayer and on servers:

- **Server** - install it so the boats exist and the fire protection applies to everyone.
- **Client** - install it to ride the lava and see the boats.

## Recipes

| Result | Recipe |
| --- | --- |
| Crimson / Warped Boat | 5 matching planks in the standard boat shape |
| Crimson / Warped Boat with Chest | a Chest + the matching boat (shapeless) |

The boats appear in the **Tools & Utilities** creative tab next to the vanilla boats.

## Building from source

Each Minecraft version lives on its own branch (`main` = newest; `*-<version>` branches for the
others). From a branch:

```sh
./gradlew build
```

The jars are written to each module's `build/libs/`. The boat textures are generated from a base
resource pack by `tools/recolor_boats.py` - see the header of that script for the inputs it expects.

## License

All Rights Reserved.
