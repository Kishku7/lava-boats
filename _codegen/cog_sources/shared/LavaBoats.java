// SHARED SOURCE -- canonical location: _codegen/cog_sources/shared. Pre-26 cell gen/ copies are
// materialized from here by scripts/cog-gen.ps1; the plain 26-shaped twin lives in shared_minecraft (keep in sync).
package com.kishku7.lavaboats;

//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_core
// compat_core.emit_recipes_imports(cog, loader, ver)
//]]]
//[[[end]]]

/** Lava Boats - shared loader-agnostic constants. RECIPES type tracks the per-version recipe-unlock API. */
public final class LavaBoats {
    public static final String MOD_ID = "lavaboats";

    //[[[cog
    // compat_core.emit_recipes_block(cog, loader, ver)
    //]]]
    //[[[end]]]

    private LavaBoats() {}
}
