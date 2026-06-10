package com.kishku7.lavaboats;

import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Lava Boats - shared constants for the 26.1.2 NeoForge build.
 *
 * 26.1.2 ships unobfuscated (mojmap-native), so NO Architectury / Architectury-Loom here -- this is
 * a standalone NeoForge ModDevGradle module with NeoForge-native registration. (See CLAUDE.md /
 * mod-publishing.md: Architectury is banned on 26.x.x+.)
 */
public final class LavaBoats {
    public static final String MOD_ID = "lavaboats";

    /** Boat recipes unlocked the moment a player joins. */
    public static final List<ResourceKey<Recipe<?>>> RECIPES = List.of(
            recipeKey("crimson_boat"),
            recipeKey("warped_boat"),
            recipeKey("crimson_chest_boat"),
            recipeKey("warped_chest_boat"));

    private static ResourceKey<Recipe<?>> recipeKey(String name) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(MOD_ID, name));
    }

    private LavaBoats() {}
}
