package com.kishku7.lavaboats.fabric.client;

import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatLayers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

/**
 * Fabric client entrypoint (26 line). Layer ids live in the shared {@link LavaBoatLayers};
 * vanilla BoatRenderer derives the texture from the layer id. Renderer registration goes
 * through vanilla {@code EntityRenderers.register} (public via Fabric transitive access
 * wideners; the fapi EntityRendererRegistry is deprecated since 1.21.9).
 */
public class LavaBoatsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(LavaBoatLayers.CRIMSON_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(LavaBoatLayers.WARPED_BOAT, BoatModel::createBoatModel);
        ModelLayerRegistry.registerModelLayer(LavaBoatLayers.CRIMSON_CHEST_BOAT, BoatModel::createChestBoatModel);
        ModelLayerRegistry.registerModelLayer(LavaBoatLayers.WARPED_CHEST_BOAT, BoatModel::createChestBoatModel);

        EntityRenderers.register(ModEntities.CRIMSON_BOAT, ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_BOAT));
        EntityRenderers.register(ModEntities.WARPED_BOAT, ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_BOAT));
        EntityRenderers.register(ModEntities.CRIMSON_CHEST_BOAT, ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_CHEST_BOAT));
        EntityRenderers.register(ModEntities.WARPED_CHEST_BOAT, ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_CHEST_BOAT));
    }
}
