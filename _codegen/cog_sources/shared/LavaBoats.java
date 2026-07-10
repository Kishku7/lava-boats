// SHARED SOURCE (single source) -- canonical + ONLY home: _codegen/cog_sources/shared.
// Every cell's gen/ copy is materialized from here by scripts/cog-gen.ps1 (no shared_minecraft java twin).
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
