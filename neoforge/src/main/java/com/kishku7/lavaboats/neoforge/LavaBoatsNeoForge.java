package com.kishku7.lavaboats.neoforge;

import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModItems;
import com.kishku7.lavaboats.neoforge.client.LavaBoatsNeoForgeClient;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(LavaBoats.MOD_ID)
public class LavaBoatsNeoForge {
    public LavaBoatsNeoForge(IEventBus modBus) {
        // NeoForge needs no EventBuses.registerModEventBus (that was the Forge-only quirk);
        // architectury-neoforge resolves the mod bus itself.
        LavaBoats.init();
        modBus.addListener(this::onBuildTabs);

        if (Platform.getEnvironment() == Env.CLIENT) {
            LavaBoatsNeoForgeClient.register(modBus);
        }
    }

    private void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ModItems.TAB_ITEMS.forEach(s -> event.accept(s.get()));
        }
    }
}
