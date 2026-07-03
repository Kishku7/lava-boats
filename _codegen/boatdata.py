"""Lava Boats -- single source of truth for the boat variants (version-aware).

Stage-1 Cog PoC, extended: every per-variant resource file is derived from this
one table, and the per-MC-version structural drift lives here as a profile() map
rather than as separate hand-maintained resource trees. The .json files stay dumb
generated artifacts (no markers, never hand-edited); this code knows what each
version needs and writes the right structure to the right path.

Resource drift across the 1.21.2 boundary (verified against on-disk trees):
  recipe dir name : recipes/  (< 1.21.2)   ->  recipe/   (>= 1.21.2)
  ingredient form : {"item": X} object     ->  "X" flat string
  result count    : "count": 1 present     ->  omitted
  item overrides  : assets/<ns>/items/ absent -> present
  models/item, lang, pack_format(34) : unchanged in this range
"""
import json

MOD_ID = "lavaboats"
WOODS = ["crimson", "warped"]
KINDS = ["boat", "chest_boat"]   # iteration order matches the hand-written lang file


def variants():
    """Yield (wood, kind, name) in the canonical order."""
    for wood in WOODS:
        for kind in KINDS:
            yield wood, kind, "{0}_{1}".format(wood, kind)


def title(wood, kind):
    w = wood.capitalize()
    return "{0} Boat".format(w) if kind == "boat" else "{0} Boat with Chest".format(w)


def profile(ver):
    """Return the resource-structure profile for an MC version tuple, e.g. (1,21,5)."""
    modern = ver >= (1, 21, 2)
    return {
        "recipe_dir": "recipe" if modern else "recipes",
        "ingredient_object": not modern,   # legacy wraps each ingredient as {"item": X}
        "result_count": not modern,         # legacy includes "count": 1
        "result_key": "id" if ver >= (1, 20, 5) else "item",  # result item->id rename at 1.20.5
        "item_overrides": modern,           # items/ override dir is 1.21.2+
    }


# ---- artifact builders (return python objects; caller serializes) ----

def _ingredient(prof, ident):
    return {"item": ident} if prof["ingredient_object"] else ident


def _result(prof, name):
    r = {prof["result_key"]: "{0}:{1}".format(MOD_ID, name)}
    if prof["result_count"]:
        r["count"] = 1
    return r


def recipe(wood, kind, name, prof):
    if kind == "boat":
        return {
            "type": "minecraft:crafting_shaped",
            "category": "misc",
            "group": "boat",
            "key": {"#": _ingredient(prof, "minecraft:{0}_planks".format(wood))},
            "pattern": ["# #", "###"],
            "result": _result(prof, name),
        }
    return {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "group": "chest_boat",
        "ingredients": [
            _ingredient(prof, "minecraft:chest"),
            _ingredient(prof, "{0}:{1}_boat".format(MOD_ID, wood)),
        ],
        "result": _result(prof, name),
    }


def item_override(name):
    return {"model": {"type": "minecraft:model", "model": "{0}:item/{1}".format(MOD_ID, name)}}


def item_model(name):
    return {"parent": "minecraft:item/generated",
            "textures": {"layer0": "{0}:item/{1}".format(MOD_ID, name)}}


def lang():
    out = {}
    for wood, kind, name in variants():
        out["item.{0}.{1}".format(MOD_ID, name)] = title(wood, kind)
    for wood, kind, name in variants():
        out["entity.{0}.{1}".format(MOD_ID, name)] = title(wood, kind)
    return out


def all_files(ver):
    """Map relative-path -> serialized JSON text for every generated resource at MC `ver`."""
    prof = profile(ver)
    files = {}
    for wood, kind, name in variants():
        files["data/{0}/{1}/{2}.json".format(MOD_ID, prof["recipe_dir"], name)] = \
            json.dumps(recipe(wood, kind, name, prof), indent=2)
        if prof["item_overrides"]:
            files["assets/{0}/items/{1}.json".format(MOD_ID, name)] = \
                json.dumps(item_override(name), indent=2)
        files["assets/{0}/models/item/{1}.json".format(MOD_ID, name)] = \
            json.dumps(item_model(name), indent=2)
    files["assets/{0}/lang/en_us.json".format(MOD_ID)] = json.dumps(lang(), indent=2)
    return files
