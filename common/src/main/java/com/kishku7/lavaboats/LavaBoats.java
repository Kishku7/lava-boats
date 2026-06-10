package com.kishku7.lavaboats;

import dev.architectury.event.events.common.PlayerEvent;

import net.minecraft.resources.ResourceLocation;

/**
 * Lava Boats - common (loader-agnostic) bootstrap.
 *
 * Registers Crimson and Warped boats (plain + chest variants) that ride on lava like water.
 * Fireproofing and rider fire-safety run server-side for every client; the lava buoyancy/steering
 * is a client-side physics tweak (see {@code mixin.client.AbstractBoatLavaMixin}).
 *
 * Called from each loader entrypoint: Fabric {@code LavaBoatsFabric}, Forge {@code LavaBoatsForge}.
 */
public final class LavaBoats {
    public static final String MOD_ID = "lavaboats";

    /** Our crafting recipes, unlocked on join so they show in the recipe book immediately. */
    private static final ResourceLocation[] RECIPES = {
            new ResourceLocation(MOD_ID, "crimson_boat"),
            new ResourceLocation(MOD_ID, "warped_boat"),
            new ResourceLocation(MOD_ID, "crimson_chest_boat"),
            new ResourceLocation(MOD_ID, "warped_chest_boat"),
    };

    private LavaBoats() {}

    public static void init() {
        // Entities first so the item suppliers resolve against committed entity types.
        ModEntities.init();
        ModItems.init();

        // Recipe discovery: unlock the boat recipes the moment a player joins, so they appear in the
        // recipe book right away (the recipes have no in-world unlock trigger of their own).
        PlayerEvent.PLAYER_JOIN.register(player -> player.awardRecipesByKey(RECIPES));
    }
}
