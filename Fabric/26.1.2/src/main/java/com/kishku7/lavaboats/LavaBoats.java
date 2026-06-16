package com.kishku7.lavaboats;

import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Lava Boats - common entrypoint.
 *
 * Registers Crimson and Warped boats (plain + chest variants) that ride on lava
 * like water. The fireproofing and rider fire-safety run server-side for every
 * client; the actual lava buoyancy/steering is a client-side physics tweak
 * (see {@code mixin.client.AbstractBoatLavaMixin}).
 */
public class LavaBoats implements ModInitializer {
    public static final String MOD_ID = "lavaboats";

    /** Our crafting recipes, unlocked on join so they show in the recipe book immediately. */
    private static final List<ResourceKey<Recipe<?>>> RECIPES = List.of(
            recipeKey("crimson_boat"),
            recipeKey("warped_boat"),
            recipeKey("crimson_chest_boat"),
            recipeKey("warped_chest_boat"));

    private static ResourceKey<Recipe<?>> recipeKey(String name) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    @Override
    public void onInitialize() {
        // Order matters: entity types reference item drops via a lazy supplier,
        // items reference the (already-registered) entity types directly.
        ModEntities.register();
        ModItems.register();

        // Recipe discovery: unlock the boat recipes the moment a player joins, so they appear in the
        // recipe book right away (the recipes have no in-world unlock trigger of their own).
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                handler.player.awardRecipesByKey(RECIPES));
    }
}
