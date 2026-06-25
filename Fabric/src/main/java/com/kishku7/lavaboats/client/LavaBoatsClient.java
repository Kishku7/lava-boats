package com.kishku7.lavaboats.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.Identifier;

public class LavaBoatsClient implements ClientModInitializer {

    // Texture is derived by BoatRenderer from the layer id:
    //   lavaboats:boat/crimson  ->  lavaboats:textures/entity/boat/crimson.png
    public static final ModelLayerLocation CRIMSON_BOAT = layer("boat/crimson");
    public static final ModelLayerLocation WARPED_BOAT = layer("boat/warped");
    public static final ModelLayerLocation CRIMSON_CHEST_BOAT = layer("chest_boat/crimson");
    public static final ModelLayerLocation WARPED_CHEST_BOAT = layer("chest_boat/warped");

    private static ModelLayerLocation layer(String path) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, path), "main");
    }

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(CRIMSON_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(WARPED_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(CRIMSON_CHEST_BOAT, BoatModel::createChestBoatModel);
        ModelLayerRegistry.registerModelLayer(WARPED_CHEST_BOAT, BoatModel::createChestBoatModel);

        EntityRendererRegistry.register(ModEntities.CRIMSON_BOAT, ctx -> new BoatRenderer(ctx, CRIMSON_BOAT));
        EntityRendererRegistry.register(ModEntities.WARPED_BOAT, ctx -> new BoatRenderer(ctx, WARPED_BOAT));
        EntityRendererRegistry.register(ModEntities.CRIMSON_CHEST_BOAT, ctx -> new BoatRenderer(ctx, CRIMSON_CHEST_BOAT));
        EntityRendererRegistry.register(ModEntities.WARPED_CHEST_BOAT, ctx -> new BoatRenderer(ctx, WARPED_CHEST_BOAT));
    }
}
