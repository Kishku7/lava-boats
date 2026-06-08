# Lava Boats

Ride lava like it's water.

**Lava Boats** adds **Crimson** and **Warped** boats — in both regular and chest variants — that
float and steer on lava exactly the way ordinary boats do on water. The boats never burn, their
passengers never catch fire, and a dropped boat bobs back up to the lava surface instead of sinking.

Built for **Minecraft 26.1.2** on **Fabric**.

## Features

- **Four new boats** — Crimson and Warped, each with a chest variant — crafted from their matching
  nether-stem planks just like vanilla boats (5 planks for a boat; chest + boat for a chest boat).
- **Lava travel** — nether-stem boats float on the lava surface and move at full water speed. Place
  one on lava and go.
- **Fireproof** — the boats are immune to fire and lava; they won't burn or break.
- **Safe passengers** — anyone riding a lava boat takes no fire or lava damage and shows no flames.
- **Floating drops** — break a boat over lava (or drop one in) and the item rises straight back to
  the surface, fire-resistant like Netherite.
- **High-quality art** — 128× boat textures with PBR (normal + specular) maps, recoloured to match
  the Crimson and Warped plank palettes. The chest on a chest boat still looks like a chest.

## How it works

Minecraft simulates a ridden boat's movement on the *controlling player's client*, so the
lava-as-water buoyancy is a client-side tweak: the boat's fluid checks are taught to treat lava like
water for nether-stem boats, and vanilla's own float/steer logic does the rest, with a small lift so
the rider sits clear of the surface. The fireproofing and passenger safety are server-side (the boat
entities are registered fire-immune, and riders report as fire-immune while seated), so they protect
everyone — even players without the mod, who simply won't get the lava buoyancy.

No new networking and no custom packets, so a vanilla client can still connect to a server running
Lava Boats.

## Installation

Requires [**Fabric Loader**](https://fabricmc.net/) and [**Fabric API**](https://modrinth.com/mod/fabric-api).

Drop the `lava-boats-*.jar` into your `mods/` folder. Works in singleplayer and on both client and
server:

- **Server** — install it so the boats exist and the fire protection applies to everyone.
- **Client** — install it to ride the lava and see the boats.

## Recipes

| Result | Recipe |
| --- | --- |
| Crimson / Warped Boat | 5 matching planks in the standard boat shape |
| Crimson / Warped Boat with Chest | a Chest + the matching boat (shapeless) |

The boats appear in the **Tools & Utilities** creative tab next to the vanilla boats.

## Building from source

```sh
./gradlew build
```

The jar is written to `build/libs/`. The boat textures are generated from a base resource pack by
`tools/recolor_boats.py` — see the header of that script for the inputs it expects (a pack
containing the vanilla boat art plus the Crimson/Warped plank textures).

## License

All Rights Reserved.
