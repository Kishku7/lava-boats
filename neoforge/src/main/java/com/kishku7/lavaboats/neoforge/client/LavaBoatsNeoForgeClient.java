package com.kishku7.lavaboats.neoforge.client;

import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatLayers;

import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class LavaBoatsNeoForgeClient {
    private LavaBoatsNeoForgeClient() {}

    /** Called from the mod constructor on the client dist only. */
    public static void register(IEventBus modBus) {
        modBus.addListener(LavaBoatsNeoForgeClient::onRegisterLayers);
        modBus.addListener(LavaBoatsNeoForgeClient::onRegisterRenderers);
    }

    private static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LavaBoatLayers.CRIMSON_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(LavaBoatLayers.WARPED_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(LavaBoatLayers.CRIMSON_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(LavaBoatLayers.WARPED_CHEST_BOAT, BoatModel::createChestBoatModel);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CRIMSON_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_BOAT));
        event.registerEntityRenderer(ModEntities.WARPED_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_BOAT));
        event.registerEntityRenderer(ModEntities.CRIMSON_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_CHEST_BOAT));
        event.registerEntityRenderer(ModEntities.WARPED_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_CHEST_BOAT));
    }
}
