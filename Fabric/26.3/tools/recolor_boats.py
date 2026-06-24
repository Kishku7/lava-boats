"""
Recolor base (birch) boat artwork to Crimson / Warped, matching plank palettes.

Inputs (a resource pack containing, under assets/minecraft/textures/):
  entity/boat/birch.png(+_n,_s), entity/chest_boat/birch.png(+_n,_s),
  item/birch_boat.png(+_n,_s), item/birch_chest_boat.png(+_n,_s),
  block/crimson_planks.png, block/warped_planks.png
Point at the pack with the RB_PACK env var or the first CLI arg
(default: a folder named "RB_128x" next to where you run this).

Method:
  * Build a brightness(V) -> (Hue, Saturation) lookup from each plank texture.
  * Match the boat's Value distribution to the plank's (mean + contrast) so the tone
    lands on the plank colour while keeping the boat's shading/detail.
  * On chest boats, the chest is left as the original (it reads as a normal chest):
    detected by saturation, with the metal lock filled in via a bounded flood-fill.
Albedo only; the _n (normal) and _s (specular) PBR maps are copied unchanged.

Output goes into this mod's assets, resolved relative to this script.
"""
import os
import sys
import shutil
import numpy as np
from PIL import Image

PACK = os.environ.get("RB_PACK") or (sys.argv[1] if len(sys.argv) > 1 else "RB_128x")
RB = os.path.join(PACK, "assets", "minecraft", "textures")
HERE = os.path.dirname(os.path.abspath(__file__))
MOD = os.path.normpath(os.path.join(HERE, "..", "src", "main", "resources", "assets", "lavaboats", "textures"))

PLANKS = {
    "crimson": os.path.join(RB, "block", "crimson_planks.png"),
    "warped":  os.path.join(RB, "block", "warped_planks.png"),
}

# (source, dest, preserve_chest)
def jobs(wood):
    return [
        (os.path.join(RB, "entity", "boat", "birch.png"),
         os.path.join(MOD, "entity", "boat", f"{wood}.png"), False),
        (os.path.join(RB, "entity", "chest_boat", "birch.png"),
         os.path.join(MOD, "entity", "chest_boat", f"{wood}.png"), True),
        (os.path.join(RB, "item", "birch_boat.png"),
         os.path.join(MOD, "item", f"{wood}_boat.png"), False),
        (os.path.join(RB, "item", "birch_chest_boat.png"),
         os.path.join(MOD, "item", f"{wood}_chest_boat.png"), True),
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

CHEST_SAT_THRESHOLD = 110  # hull sits ~100; chest orange wood is well above

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
            lut_h[v] = circ_mean_255(H[mask]); lut_s[v] = S[mask].mean()
    filled = np.where(lut_s >= 0)[0]
    for v in range(256):
        if lut_s[v] < 0:
            nn = filled[np.argmin(np.abs(filled - v))]
            lut_h[v] = lut_h[nn]; lut_s[v] = lut_s[nn]
    return lut_h, lut_s, float(V.mean()), float(V.std() + 1e-6)

def chest_mask(rgba):
    a = rgba[..., 3]; op = a > 8
    S = np.asarray(Image.fromarray(rgba[..., :3], "RGB").convert("HSV"))[..., 1]
    core = op & (S > CHEST_SAT_THRESHOLD)
    if core.sum() == 0:
        return np.zeros(op.shape, bool)
    ys, xs = np.where(core)
    m = 6
    y0 = max(0, ys.min() - m); y1 = min(op.shape[0] - 1, ys.max() + m)
    x0 = max(0, xs.min() - m); x1 = min(op.shape[1] - 1, xs.max() + m)
    region = np.zeros(op.shape, bool); region[y0:y1 + 1, x0:x1 + 1] = True
    region &= ~core
    bg = np.zeros(op.shape, bool)
    bg[y0, x0:x1 + 1] |= region[y0, x0:x1 + 1]; bg[y1, x0:x1 + 1] |= region[y1, x0:x1 + 1]
    bg[y0:y1 + 1, x0] |= region[y0:y1 + 1, x0]; bg[y0:y1 + 1, x1] |= region[y0:y1 + 1, x1]
    while True:
        up = np.zeros_like(bg); up[:-1, :] = bg[1:, :]
        dn = np.zeros_like(bg); dn[1:, :] = bg[:-1, :]
        lf = np.zeros_like(bg); lf[:, :-1] = bg[:, 1:]
        rt = np.zeros_like(bg); rt[:, 1:] = bg[:, :-1]
        nb = (bg | up | dn | lf | rt) & region
        if int(nb.sum()) == int(bg.sum()):
            break
        bg = nb
    return core | (region & ~bg)

def recolor(src_path, dst_path, lut_h, lut_s, plank_vmean, plank_vstd, preserve_chest=False):
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
    Vout = np.clip((V - src_mean) * (plank_vstd / src_std) + plank_vmean, 0, 255)
    idx = Vout.astype(np.int32)
    out_hsv = np.stack([lut_h[idx], lut_s[idx], Vout], axis=-1).astype(np.uint8)
    out_rgb = np.asarray(Image.fromarray(out_hsv, "HSV").convert("RGB"))
    out = np.dstack([out_rgb, alpha]).astype(np.uint8)
    if preserve_chest:
        cm = chest_mask(rgba)
        out[cm] = rgba[cm]
    os.makedirs(os.path.dirname(dst_path), exist_ok=True)
    Image.fromarray(out, "RGBA").save(dst_path)
    return img.size

def main():
    for wood, plank in PLANKS.items():
        lut_h, lut_s, vmean, vstd = build_lut(plank)
        for src, dst, pc in jobs(wood):
            size = recolor(src, dst, lut_h, lut_s, vmean, vstd, pc)
            print(f"  recolor {wood:8s} {os.path.basename(dst):26s} {size}")
        for src_t, dst_t in PBR_SRC_DST:
            src = os.path.join(RB, *src_t)
            dst = os.path.join(MOD, *[p.format(w=wood) for p in dst_t])
            os.makedirs(os.path.dirname(dst), exist_ok=True)
            shutil.copyfile(src, dst)
    print("DONE")

if __name__ == "__main__":
    main()
