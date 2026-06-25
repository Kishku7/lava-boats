package com.kishku7.lavaboats;

import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.item.Item;

public final class ModEntities {
    private ModEntities() {}

    public static EntityType<Boat> CRIMSON_BOAT;
    public static EntityType<Boat> WARPED_BOAT;
    public static EntityType<ChestBoat> CRIMSON_CHEST_BOAT;
    public static EntityType<ChestBoat> WARPED_CHEST_BOAT;

    private static Set<EntityType<?>> LAVA_BOATS = Set.of();

    public static void register() {
        CRIMSON_BOAT = registerBoat("crimson_boat", () -> ModItems.CRIMSON_BOAT);
        WARPED_BOAT = registerBoat("warped_boat", () -> ModItems.WARPED_BOAT);
        CRIMSON_CHEST_BOAT = registerChestBoat("crimson_chest_boat", () -> ModItems.CRIMSON_CHEST_BOAT);
        WARPED_CHEST_BOAT = registerChestBoat("warped_chest_boat", () -> ModItems.WARPED_CHEST_BOAT);

        LAVA_BOATS = Set.of(CRIMSON_BOAT, WARPED_BOAT, CRIMSON_CHEST_BOAT, WARPED_CHEST_BOAT);
    }

    /** True for any of our nether-stem lava boats. Used by the physics + fire mixins. */
    public static boolean isLavaBoat(EntityType<?> type) {
        return LAVA_BOATS.contains(type);
    }

    private static EntityType<Boat> registerBoat(String name, Supplier<Item> dropItem) {
        Identifier id = Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, name);
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        EntityType<Boat> type = EntityType.Builder
                .<Boat>of((t, level) -> new Boat(t, level, dropItem), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)
                .fireImmune() // boat itself never burns / takes lava damage
                .build(key);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
    }

    private static EntityType<ChestBoat> registerChestBoat(String name, Supplier<Item> dropItem) {
        Identifier id = Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, name);
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
        EntityType<ChestBoat> type = EntityType.Builder
                .<ChestBoat>of((t, level) -> new ChestBoat(t, level, dropItem), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)
                .fireImmune()
                .build(key);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
    }
}
