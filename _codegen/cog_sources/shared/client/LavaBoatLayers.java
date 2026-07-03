// SHARED SOURCE -- canonical location: _codegen/cog_sources/shared. Pre-26 cell gen/ copies are
// materialized from here by scripts/cog-gen.ps1; the plain 26-shaped twin lives in shared_minecraft (keep in sync).
package com.kishku7.lavaboats.client;

import com.kishku7.lavaboats.LavaBoats;

import net.minecraft.client.model.geom.ModelLayerLocation;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_core
// compat_core.emit_layer_id_import(cog, loader, ver)
// compat_core.emit_environment_imports(cog, loader)
//]]]
//[[[end]]]

/** Model-layer locations for the lava boats. Vanilla BoatRenderer derives the texture from the layer id. */
//[[[cog
// compat_core.emit_environment(cog, loader)
//]]]
//[[[end]]]
public final class LavaBoatLayers {
    private LavaBoatLayers() {}

    public static final ModelLayerLocation CRIMSON_BOAT = layer("boat/crimson");
    public static final ModelLayerLocation WARPED_BOAT = layer("boat/warped");
    public static final ModelLayerLocation CRIMSON_CHEST_BOAT = layer("chest_boat/crimson");
    public static final ModelLayerLocation WARPED_CHEST_BOAT = layer("chest_boat/warped");

    private static ModelLayerLocation layer(String path) {
        //[[[cog
        // compat_core.emit_layer_body(cog, loader, ver)
        //]]]
        //[[[end]]]
    }
}
