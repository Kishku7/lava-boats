package com.kishku7.lavaboats.forge;

import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModItems;

import dev.architectury.platform.forge.EventBuses;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LavaBoats.MOD_ID)
public class LavaBoatsForge {
    public LavaBoatsForge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register this mod's event bus with Architectury BEFORE using DeferredRegister, so
        // DeferredRegister.register() can find it. (architectury-loom injects this automatically;
        // in a manual ForgeGradle build we must do it explicitly.)
        EventBuses.registerModEventBus(LavaBoats.MOD_ID, modBus);

        LavaBoats.init();
        modBus.addListener(this::onBuildTabs);
    }

    private void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ModItems.TAB_ITEMS.forEach(s -> event.accept(s.get()));
        }
    }
}
