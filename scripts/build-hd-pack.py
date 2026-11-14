"""Build the optional 'Lava Boats HD' resource pack zip from shared_minecraft/.../resourcepacks/lavaboats_hd.

Usage: python scripts/build-hd-pack.py <version>   -> dist/lava-boats-hd-pack-<version>.zip
The pack overrides the mod's vanilla-size boat + item textures with the original 4x art (and its
_n/_s PBR maps). pack.mcmeta covers every supported MC version with BOTH range spellings.
ASCII only.
"""
import json, os, sys, zipfile

root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
src = os.path.join(root, "shared_minecraft", "src", "main", "resources", "resourcepacks", "lavaboats_hd")
ver = sys.argv[1]
out = os.path.join(root, "dist", "lava-boats-hd-pack-%s.zip" % ver)
os.makedirs(os.path.dirname(out), exist_ok=True)

mcmeta = {"pack": {
    "description": "Lava Boats HD textures (optional, replaces the vanilla-style boats)",
    "pack_format": 15,
    "supported_formats": {"min_inclusive": 15, "max_inclusive": 97},
    "min_format": 15,
    "max_format": 97,
}}

with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as z:
    z.writestr("pack.mcmeta", json.dumps(mcmeta, indent=2) + "\n")
    for d, _, fs in os.walk(os.path.join(src, "assets")):
        for f in sorted(fs):
            p = os.path.join(d, f)
            z.write(p, os.path.relpath(p, src).replace(os.sep, "/"))
print(out, os.path.getsize(out), "bytes")
