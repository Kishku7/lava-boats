package com.kishku7.lavaboats.client;

import com.kishku7.lavaboats.LavaBoats;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

/**
 * Model-layer locations for the lava boats. The vanilla {@code BoatRenderer} derives the texture
 * from the layer id, e.g. {@code lavaboats:boat/crimson -> lavaboats:textures/entity/boat/crimson.png},
 * so registering these layers + a {@code BoatRenderer(ctx, layer)} per loader is all that's needed.
 */
@Environment(EnvType.CLIENT)
public final class LavaBoatLayers {
    private LavaBoatLayers() {}

    public static final ModelLayerLocation CRIMSON_BOAT = layer("boat/crimson");
    public static final ModelLayerLocation WARPED_BOAT = layer("boat/warped");
    public static final ModelLayerLocation CRIMSON_CHEST_BOAT = layer("chest_boat/crimson");
    public static final ModelLayerLocation WARPED_CHEST_BOAT = layer("chest_boat/warped");

    private static ModelLayerLocation layer(String path) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, path), "main");
    }
}
