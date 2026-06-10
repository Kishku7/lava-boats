package com.kishku7.lavaboats;

import java.util.List;

import dev.architectury.event.events.common.PlayerEvent;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Lava Boats - common (loader-agnostic) bootstrap for the 1.21.11 family (Fabric + NeoForge).
 *
 * 1.21.11 shares the 26.1.2 boat API (vehicle.boat.Boat with a drop-item supplier ctor, Identifier,
 * Item.Properties.setId, ResourceKey-based recipes), so the entity/item code matches 26.1.2; only
 * registration is wrapped in Architectury for multiloader.
 */
public final class LavaBoats {
    public static final String MOD_ID = "lavaboats";

    private static final List<ResourceKey<Recipe<?>>> RECIPES = List.of(
            recipeKey("crimson_boat"),
            recipeKey("warped_boat"),
            recipeKey("crimson_chest_boat"),
            recipeKey("warped_chest_boat"));

    private static ResourceKey<Recipe<?>> recipeKey(String name) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    private LavaBoats() {}

    public static void init() {
        ModEntities.init();
        ModItems.init();

        // Recipe discovery: unlock the boat recipes the moment a player joins.
        PlayerEvent.PLAYER_JOIN.register(player -> player.awardRecipesByKey(RECIPES));
    }
}
