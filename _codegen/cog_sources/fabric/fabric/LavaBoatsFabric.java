// SHARED SOURCE -- canonical location: _codegen/cog_sources/fabric. Cell gen/ copies are
// materialized from here by scripts/cog-gen.ps1; edit ONLY this copy.
package com.kishku7.lavaboats.fabric;

import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.ModItems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.item.CreativeModeTabs;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_fabric as compat
// compat.emit_join_unlock_imports(cog, ver)
//]]]
//[[[end]]]

/**
 * Fabric entrypoint. STANDALONE native registration (no Architectury): entities then items.
 */
public class LavaBoatsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Entities first so the item suppliers resolve against committed entity types.
        ModEntities.init();
        ModItems.init();

        // Slot the boats into the Tools & Utilities tab alongside the vanilla boats.
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries ->
                ModItems.TAB_ITEMS.forEach(entries::accept));

        // Recipe discovery: unlock the boat recipes the moment a player joins, so they appear in
        // the recipe book right away (no in-world unlock trigger of their own).
        //[[[cog
        // compat.emit_join_unlock(cog, ver)
        //]]]
        //[[[end]]]
    }
}
