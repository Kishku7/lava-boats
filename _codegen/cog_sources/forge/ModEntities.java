package com.kishku7.lavaboats;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_forge as compat
// cog.outl("import net.minecraft.resources." + compat.id_type(ver) + ";")
// for _n in ["Boat", "ChestBoat"]: cog.outl("import " + compat.boat_pkg(ver) + "." + _n + ";")
// cog.outl("import " + compat.bus_import(ver) + ";")
//]]]
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraftforge.eventbus.api.bus.BusGroup;
//[[[end]]]
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Forge entity registration (DeferredRegister). Vanilla Boat/ChestBoat both eras; legacy drop via BoatDropMixin. */
public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LavaBoats.MOD_ID);

    public static final RegistryObject<EntityType<Boat>> CRIMSON_BOAT = registerBoat("crimson_boat", () -> ModItems.CRIMSON_BOAT.get());
    public static final RegistryObject<EntityType<Boat>> WARPED_BOAT = registerBoat("warped_boat", () -> ModItems.WARPED_BOAT.get());
    public static final RegistryObject<EntityType<ChestBoat>> CRIMSON_CHEST_BOAT = registerChestBoat("crimson_chest_boat", () -> ModItems.CRIMSON_CHEST_BOAT.get());
    public static final RegistryObject<EntityType<ChestBoat>> WARPED_CHEST_BOAT = registerChestBoat("warped_chest_boat", () -> ModItems.WARPED_CHEST_BOAT.get());

    //[[[cog
    // compat.emit_entities_register(cog, ver)
    //]]]
    public static void register(BusGroup modBus) {
        ENTITIES.register(modBus);
    }
    //[[[end]]]

    //[[[cog
    // import compat_core; compat_core.emit_is_lava_boat(cog)
    //]]]
    public static boolean isLavaBoat(EntityType<?> type) {
        return type == CRIMSON_BOAT.get() || type == WARPED_BOAT.get()
                || type == CRIMSON_CHEST_BOAT.get() || type == WARPED_CHEST_BOAT.get();
    }
    //[[[end]]]

    /** Drop-item lookup for the legacy drop mixin; harmless if unused on modern. */
    //[[[cog
    // import compat_core; compat_core.emit_drop_item_for(cog)
    //]]]
    public static Item dropItemFor(EntityType<?> type) {
        if (type == CRIMSON_BOAT.get()) return ModItems.CRIMSON_BOAT.get();
        if (type == WARPED_BOAT.get()) return ModItems.WARPED_BOAT.get();
        if (type == CRIMSON_CHEST_BOAT.get()) return ModItems.CRIMSON_CHEST_BOAT.get();
        if (type == WARPED_CHEST_BOAT.get()) return ModItems.WARPED_CHEST_BOAT.get();
        return null;
    }
    //[[[end]]]

    private static RegistryObject<EntityType<Boat>> registerBoat(String name, Supplier<Item> dropItem) {
        //[[[cog
        // compat.emit_register_boat_body(cog, ver, "boat")
        //]]]
        return ENTITIES.register(name, () -> EntityType.Builder
                .<Boat>of((t, level) -> new Boat(t, level, dropItem), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)
                .fireImmune()
                .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LavaBoats.MOD_ID, name))));
        //[[[end]]]
    }

    private static RegistryObject<EntityType<ChestBoat>> registerChestBoat(String name, Supplier<Item> dropItem) {
        //[[[cog
        // compat.emit_register_boat_body(cog, ver, "chest")
        //]]]
        return ENTITIES.register(name, () -> EntityType.Builder
                .<ChestBoat>of((t, level) -> new ChestBoat(t, level, dropItem), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)
                .fireImmune()
                .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(LavaBoats.MOD_ID, name))));
        //[[[end]]]
    }
}
