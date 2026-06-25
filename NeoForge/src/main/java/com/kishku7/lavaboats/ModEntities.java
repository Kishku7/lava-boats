package com.kishku7.lavaboats;

import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, LavaBoats.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<Boat>> CRIMSON_BOAT =
            registerBoat("crimson_boat", () -> ModItems.CRIMSON_BOAT.get());
    public static final DeferredHolder<EntityType<?>, EntityType<Boat>> WARPED_BOAT =
            registerBoat("warped_boat", () -> ModItems.WARPED_BOAT.get());
    public static final DeferredHolder<EntityType<?>, EntityType<ChestBoat>> CRIMSON_CHEST_BOAT =
            registerChestBoat("crimson_chest_boat", () -> ModItems.CRIMSON_CHEST_BOAT.get());
    public static final DeferredHolder<EntityType<?>, EntityType<ChestBoat>> WARPED_CHEST_BOAT =
            registerChestBoat("warped_chest_boat", () -> ModItems.WARPED_CHEST_BOAT.get());

    public static boolean isLavaBoat(EntityType<?> type) {
        return type == CRIMSON_BOAT.get() || type == WARPED_BOAT.get()
                || type == CRIMSON_CHEST_BOAT.get() || type == WARPED_CHEST_BOAT.get();
    }

    private static DeferredHolder<EntityType<?>, EntityType<Boat>> registerBoat(String name, Supplier<Item> dropItem) {
        return ENTITIES.register(name, () -> EntityType.Builder
                .<Boat>of((t, level) -> new Boat(t, level, dropItem), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)
                .fireImmune()
                .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, name))));
    }

    private static DeferredHolder<EntityType<?>, EntityType<ChestBoat>> registerChestBoat(String name, Supplier<Item> dropItem) {
        return ENTITIES.register(name, () -> EntityType.Builder
                .<ChestBoat>of((t, level) -> new ChestBoat(t, level, dropItem), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)
                .fireImmune()
                .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, name))));
    }
}
