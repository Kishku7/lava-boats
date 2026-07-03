"""Regenerate the per-variant resource files for a given MC version into a target resources dir.
Usage: python gen_resources.py <a.b.c> <resourcesDir>
Clears the recipe/recipes + items + models/item dirs first so stale (wrong-version) files never linger.
The recipe DIR name and schema, item-override presence, etc. follow boatdata.profile(version).
"""
import sys, os, json, shutil
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import boatdata

def main(ver, resdir):
    vt = tuple(int(x) for x in ver.split("."))
    ns = boatdata.MOD_ID
    # clear both recipe dir spellings + items + models/item so no stale files survive a version swap
    for d in [os.path.join(resdir, "data", ns, "recipe"),
              os.path.join(resdir, "data", ns, "recipes"),
              os.path.join(resdir, "assets", ns, "items"),
              os.path.join(resdir, "assets", ns, "models", "item")]:
        shutil.rmtree(d, ignore_errors=True)
    for rel, text in boatdata.all_files(vt).items():
        dest = os.path.join(resdir, rel.replace("/", os.sep))
        os.makedirs(os.path.dirname(dest), exist_ok=True)
        with open(dest, "w", encoding="utf-8") as f:
            f.write(text + "\n")

if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])