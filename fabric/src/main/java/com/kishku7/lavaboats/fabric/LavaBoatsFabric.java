package com.kishku7.lavaboats.fabric;

import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModItems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class LavaBoatsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LavaBoats.init();

        // Slot the boats into the Tools & Utilities tab alongside the vanilla boats.
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries ->
                ModItems.TAB_ITEMS.forEach(s -> entries.accept(s.get())));
    }
}
