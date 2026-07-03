package com.kishku7.lavaboats.neoforge;

//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_neoforge as compat
// compat.emit_entrypoint_file(cog, ver)
//]]]
import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.ModItems;
import com.kishku7.lavaboats.neoforge.client.LavaBoatsNeoForgeClient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(LavaBoats.MOD_ID)
public class LavaBoatsNeoForge {
    public LavaBoatsNeoForge(IEventBus modBus) {
        ModEntities.register(modBus);
        ModItems.register(modBus);

        modBus.addListener(this::onBuildTabs);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);

        if (FMLLoader.getCurrent().getDist() == Dist.CLIENT) {
            LavaBoatsNeoForgeClient.register(modBus);
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
