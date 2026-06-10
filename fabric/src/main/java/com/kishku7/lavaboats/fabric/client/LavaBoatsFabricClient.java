package com.kishku7.lavaboats.fabric.client;

import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class LavaBoatsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.CRIMSON_BOAT.get(),
                ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.CRIMSON, false));
        EntityRendererRegistry.register(ModEntities.WARPED_BOAT.get(),
                ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.WARPED, false));
        EntityRendererRegistry.register(ModEntities.CRIMSON_CHEST_BOAT.get(),
                ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.CRIMSON_CHEST, true));
        EntityRendererRegistry.register(ModEntities.WARPED_CHEST_BOAT.get(),
                ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.WARPED_CHEST, true));
    }
}
