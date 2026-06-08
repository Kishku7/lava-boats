"""
Recolor birch boat artwork to crimson / warped, matching the RB_128x plank palettes.

Method:
  * Build a brightness(V) -> (Hue, Saturation) lookup from the plank texture.
  * Match the boat's Value distribution to the plank's (mean + contrast) so the
    overall tone lands where the planks are (deep crimson / teal warped) instead
    of staying birch-pale. Relative shading/detail is preserved.
  * Index hue+saturation by the *remapped* value so dark areas pick up the plank's
    dark tones and highlights pick up its highlights.
Albedo only; _n (normal) and _s (specular) PBR maps are copied unchanged.
"""
import os
import shutil
import numpy as np
from PIL import Image

RB = r"C:\Users\user\Local_Research\Minecraft\resourcePacks\RB_128x\assets\minecraft\textures"
MOD = r"C:\Users\user\Local_Research\Minecraft\mods\lava-boats\src\main\resources\assets\lavaboats\textures"

PLANKS = {
    "crimson": os.path.join(RB, "block", "crimson_planks.png"),
    "warped":  os.path.join(RB, "block", "warped_planks.png"),
}

def jobs(wood):
    return [
        (os.path.join(RB, "entity", "boat", "birch.png"),
         os.path.join(MOD, "entity", "boat", f"{wood}.png")),
        (os.path.join(RB, "entity", "chest_boat", "birch.png"),
         os.path.join(MOD, "entity", "chest_boat", f"{wood}.png")),
        (os.path.join(RB, "item", "birch_boat.png"),
         os.path.join(MOD, "item", f"{wood}_boat.png")),
        (os.path.join(RB, "item", "birch_chest_boat.png"),
         os.path.join(MOD, "item", f"{wood}_chest_boat.png")),
    ]

PBR_SRC_DST = [
    (("entity", "boat", "birch_n.png"),       ("entity", "boat", "{w}_n.png")),
    (("entity", "boat", "birch_s.png"),       ("entity", "boat", "{w}_s.png")),
    (("entity", "chest_boat", "birch_n.png"), ("entity", "chest_boat", "{w}_n.png")),
    (("entity", "chest_boat", "birch_s.png"), ("entity", "chest_boat", "{w}_s.png")),
    (("item", "birch_boat_n.png"),            ("item", "{w}_boat_n.png")),
    (("item", "birch_boat_s.png"),            ("item", "{w}_boat_s.png")),
    (("item", "birch_chest_boat_n.png"),      ("item", "{w}_chest_boat_n.png")),
    (("item", "birch_chest_boat_s.png"),      ("item", "{w}_chest_boat_s.png")),
]

def circ_mean_255(hues_255):
    ang = hues_255.astype(np.float64) / 255.0 * 2.0 * np.pi
    m = np.arctan2(np.sin(ang).sum(), np.cos(ang).sum())
    if m < 0:
        m += 2.0 * np.pi
    return m / (2.0 * np.pi) * 255.0

def build_lut(plank_path):
    hsv = np.asarray(Image.open(plank_path).convert("RGB").convert("HSV"))
    H = hsv[..., 0].reshape(-1); S = hsv[..., 1].reshape(-1); V = hsv[..., 2].reshape(-1)
    lut_h = np.full(256, -1.0); lut_s = np.full(256, -1.0)
    for v in range(256):
        mask = V == v
        if mask.sum() >= 1:
            lut_h[v] = circ_mean_255(H[mask])
            lut_s[v] = S[mask].mean()
    filled = np.where(lut_s >= 0)[0]
    for v in range(256):
        if lut_s[v] < 0:
            nn = filled[np.argmin(np.abs(filled - v))]
            lut_h[v] = lut_h[nn]; lut_s[v] = lut_s[nn]
    # plank value stats (weighted toward the mid-body, ignore pure black/!alpha none here)
    return lut_h, lut_s, float(V.mean()), float(V.std() + 1e-6)

def recolor(src_path, dst_path, lut_h, lut_s, plank_vmean, plank_vstd, sat_boost=1.0):
    img = Image.open(src_path).convert("RGBA")
    rgba = np.asarray(img)
    alpha = rgba[..., 3]
    opaque = alpha > 8
    hsv = np.asarray(Image.fromarray(rgba[..., :3], "RGB").convert("HSV")).astype(np.float64)
    V = hsv[..., 2]
    if opaque.sum() > 0:
        src_mean = V[opaque].mean(); src_std = V[opaque].std() + 1e-6
    else:
        src_mean, src_std = V.mean(), V.std() + 1e-6
    # match boat value distribution to plank's (keeps relative shading, shifts tone)
    Vout = (V - src_mean) * (plank_vstd / src_std) + plank_vmean
    Vout = np.clip(Vout, 0, 255)
    idx = Vout.astype(np.int32)
    new_h = lut_h[idx]
    new_s = np.clip(lut_s[idx] * sat_boost, 0, 255)
    out_hsv = np.stack([new_h, new_s, Vout], axis=-1).astype(np.uint8)
    out_rgb = np.asarray(Image.fromarray(out_hsv, "HSV").convert("RGB"))
    out = np.dstack([out_rgb, alpha]).astype(np.uint8)
    os.makedirs(os.path.dirname(dst_path), exist_ok=True)
    Image.fromarray(out, "RGBA").save(dst_path)
    return img.size

def main():
    for wood, plank in PLANKS.items():
        lut_h, lut_s, vmean, vstd = build_lut(plank)
        for src, dst in jobs(wood):
            size = recolor(src, dst, lut_h, lut_s, vmean, vstd)
            print(f"  recolor {wood:8s} {os.path.basename(dst):26s} {size}  plankV(mean={vmean:.0f},std={vstd:.0f})")
        for src_t, dst_t in PBR_SRC_DST:
            src = os.path.join(RB, *src_t)
            dst = os.path.join(MOD, *[p.format(w=wood) for p in dst_t])
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            shutil.copyfile(src, dst)
    print("DONE")

if __name__ == "__main__":
    main()
