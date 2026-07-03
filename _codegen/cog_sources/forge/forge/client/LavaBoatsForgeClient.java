package com.kishku7.lavaboats.forge.client;

//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_forge as compat
// compat.emit_client_file(cog, ver)
//]]]
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatLayers;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

public final class LavaBoatsForgeClient {
    private LavaBoatsForgeClient() {}

    public static void register(BusGroup modBus) {
        EntityRenderersEvent.RegisterLayerDefinitions.getBus(modBus).addListener(LavaBoatsForgeClient::onRegisterLayers);
        EntityRenderersEvent.RegisterRenderers.getBus(modBus).addListener(LavaBoatsForgeClient::onRegisterRenderers);
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
//[[[end]]]
