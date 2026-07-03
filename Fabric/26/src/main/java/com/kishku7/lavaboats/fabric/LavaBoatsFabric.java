package com.kishku7.lavaboats.fabric;

import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.ModItems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * Fabric entrypoint (26 line). STANDALONE native registration: entities then items.
 * Constants + recipe keys live in the shared {@link LavaBoats}; the creative-tab insertion
 * is inside {@link ModItems#register()} (26 Fabric API: CreativeModeTabEvents).
 */
public class LavaBoatsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Entities first so the item suppliers resolve against committed entity types.
        ModEntities.register();
        ModItems.register();

        // Recipe discovery: unlock the boat recipes the moment a player joins, so they appear in
        // the recipe book right away (no in-world unlock trigger of their own).
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                handler.player.awardRecipesByKey(LavaBoats.RECIPES));
    }
}
