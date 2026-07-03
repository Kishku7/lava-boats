package com.kishku7.lavaboats.fabric.client;

//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_fabric as compat
// compat.emit_client_imports(cog, loader, ver)
//]]]
import com.kishku7.lavaboats.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.kishku7.lavaboats.client.LavaBoatLayers;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.model.object.boat.BoatModel;
//[[[end]]]

public class LavaBoatsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //[[[cog
        // compat.emit_client_body(cog, loader, ver)
        //]]]
        EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.CRIMSON_BOAT, BoatModel::createBoatModel);
        EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.WARPED_BOAT, BoatModel::createBoatModel);
        EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.CRIMSON_CHEST_BOAT, BoatModel::createChestBoatModel);
        EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.WARPED_CHEST_BOAT, BoatModel::createChestBoatModel);
        EntityRendererRegistry.register(ModEntities.CRIMSON_BOAT, ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_BOAT));
        EntityRendererRegistry.register(ModEntities.WARPED_BOAT, ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_BOAT));
        EntityRendererRegistry.register(ModEntities.CRIMSON_CHEST_BOAT, ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_CHEST_BOAT));
        EntityRendererRegistry.register(ModEntities.WARPED_CHEST_BOAT, ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_CHEST_BOAT));
        //[[[end]]]
    }
}
