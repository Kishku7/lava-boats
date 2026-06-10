package com.kishku7.lavaboats.forge.client;

import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatRenderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LavaBoats.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LavaBoatsForgeClient {
    private LavaBoatsForgeClient() {}

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CRIMSON_BOAT.get(),
                ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.CRIMSON, false));
        event.registerEntityRenderer(ModEntities.WARPED_BOAT.get(),
                ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.WARPED, false));
        event.registerEntityRenderer(ModEntities.CRIMSON_CHEST_BOAT.get(),
                ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.CRIMSON_CHEST, true));
        event.registerEntityRenderer(ModEntities.WARPED_CHEST_BOAT.get(),
                ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.WARPED_CHEST, true));
    }
}
