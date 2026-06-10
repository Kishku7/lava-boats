package com.kishku7.lavaboats.fabric.client;

import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatLayers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;

public class LavaBoatsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.CRIMSON_BOAT, BoatModel::createBoatModel);
        EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.WARPED_BOAT, BoatModel::createBoatModel);
        EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.CRIMSON_CHEST_BOAT, BoatModel::createChestBoatModel);
        EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.WARPED_CHEST_BOAT, BoatModel::createChestBoatModel);

        EntityRendererRegistry.register(ModEntities.CRIMSON_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_BOAT));
        EntityRendererRegistry.register(ModEntities.WARPED_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_BOAT));
        EntityRendererRegistry.register(ModEntities.CRIMSON_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_CHEST_BOAT));
        EntityRendererRegistry.register(ModEntities.WARPED_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_CHEST_BOAT));
    }
}
