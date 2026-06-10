package com.kishku7.lavaboats;

import java.util.function.Supplier;

import com.kishku7.lavaboats.entity.LavaBoat;
import com.kishku7.lavaboats.entity.LavaChestBoat;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(LavaBoats.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<LavaBoat>> CRIMSON_BOAT =
            registerBoat("crimson_boat", () -> ModItems.CRIMSON_BOAT.get());
    public static final RegistrySupplier<EntityType<LavaBoat>> WARPED_BOAT =
            registerBoat("warped_boat", () -> ModItems.WARPED_BOAT.get());
    public static final RegistrySupplier<EntityType<LavaChestBoat>> CRIMSON_CHEST_BOAT =
            registerChestBoat("crimson_chest_boat", () -> ModItems.CRIMSON_CHEST_BOAT.get());
    public static final RegistrySupplier<EntityType<LavaChestBoat>> WARPED_CHEST_BOAT =
            registerChestBoat("warped_chest_boat", () -> ModItems.WARPED_CHEST_BOAT.get());

    public static void init() {
        ENTITIES.register();
    }

    /** True for any of our nether-stem lava boats. Used by the physics + fire mixins. */
    public static boolean isLavaBoat(EntityType<?> type) {
        return type == CRIMSON_BOAT.get() || type == WARPED_BOAT.get()
                || type == CRIMSON_CHEST_BOAT.get() || type == WARPED_CHEST_BOAT.get();
    }

    private static RegistrySupplier<EntityType<LavaBoat>> registerBoat(String name, Supplier<Item> dropItem) {
        return ENTITIES.register(name, () -> EntityType.Builder
                .<LavaBoat>of((type, level) -> new LavaBoat(type, level, dropItem), MobCategory.MISC)
                .sized(1.375F, 0.5625F)
                .clientTrackingRange(10)
                .fireImmune() // boat itself never burns / takes lava damage
                .build(name));
    }

    private static RegistrySupplier<EntityType<LavaChestBoat>> registerChestBoat(String name, Supplier<Item> dropItem) {
        return ENTITIES.register(name, () -> EntityType.Builder
                .<LavaChestBoat>of((type, level) -> new LavaChestBoat(type, level, dropItem), MobCategory.MISC)
                .sized(1.375F, 0.5625F)
                .clientTrackingRange(10)
                .fireImmune()
                .build(name));
    }
}
