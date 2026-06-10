package com.kishku7.lavaboats;

import com.kishku7.lavaboats.client.LavaBoatsNeoForgeClient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(LavaBoats.MOD_ID)
public class LavaBoatsNeoForge {

    public LavaBoatsNeoForge(ModContainer mod, IEventBus bus, Dist dist) {
        ModEntities.ENTITIES.register(bus);
        ModItems.ITEMS.register(bus);

        bus.addListener(this::onBuildTabs);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);

        if (dist.isClient()) {
            LavaBoatsNeoForgeClient.init(bus);
        }
    }

    private void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ModItems.TAB_ITEMS.forEach(holder -> event.accept(holder.get()));
        }
    }

    /** Recipe discovery: unlock the boat recipes the moment a player joins. */
    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.awardRecipesByKey(LavaBoats.RECIPES);
        }
    }
}
