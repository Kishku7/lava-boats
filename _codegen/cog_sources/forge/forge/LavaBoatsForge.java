package com.kishku7.lavaboats.forge;

//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_forge as compat
// compat.emit_entrypoint_file(cog, ver)
//]]]
import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.ModItems;
import com.kishku7.lavaboats.forge.client.LavaBoatsForgeClient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(LavaBoats.MOD_ID)
public class LavaBoatsForge {
    public LavaBoatsForge(FMLJavaModLoadingContext context) {
        BusGroup modBus = context.getModBusGroup();
        ModEntities.register(modBus);
        ModItems.register(modBus);
        BuildCreativeModeTabContentsEvent.getBus(modBus).addListener(this::onBuildTabs);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(this::onPlayerJoin);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            LavaBoatsForgeClient.register(modBus);
        }
    }
    private void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ModItems.TAB_ITEMS.forEach(s -> event.accept(s.get()));
        }
    }
    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.awardRecipesByKey(LavaBoats.RECIPES);
        }
    }
}
//[[[end]]]
