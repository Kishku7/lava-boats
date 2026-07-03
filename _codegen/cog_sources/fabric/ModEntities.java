package com.kishku7.lavaboats;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_fabric as compat
// for _n in ["Boat", "ChestBoat"]: cog.outl("import " + compat.boat_pkg(ver) + "." + _n + ";")
//]]]
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
//[[[end]]]
import net.minecraft.world.item.Item;

/** Entity registration. Vanilla Boat/ChestBoat both eras; the drop is the supplier ctor (>=1.21.2)
 *  or the legacy BoatDropMixin (pre-1.21.2). No custom subclasses. */
public final class ModEntities {
    private ModEntities() {}

    public static EntityType<Boat> CRIMSON_BOAT;
    public static EntityType<Boat> WARPED_BOAT;
    public static EntityType<ChestBoat> CRIMSON_CHEST_BOAT;
    public static EntityType<ChestBoat> WARPED_CHEST_BOAT;

    public static void init() {
        CRIMSON_BOAT = registerBoat("crimson_boat", () -> ModItems.CRIMSON_BOAT);
        WARPED_BOAT = registerBoat("warped_boat", () -> ModItems.WARPED_BOAT);
        CRIMSON_CHEST_BOAT = registerChestBoat("crimson_chest_boat", () -> ModItems.CRIMSON_CHEST_BOAT);
        WARPED_CHEST_BOAT = registerChestBoat("warped_chest_boat", () -> ModItems.WARPED_CHEST_BOAT);
    }

    public static boolean isLavaBoat(EntityType<?> type) {
        return type == CRIMSON_BOAT || type == WARPED_BOAT
                || type == CRIMSON_CHEST_BOAT || type == WARPED_CHEST_BOAT;
    }

    /** Drop-item lookup for the legacy drop mixin; harmless if unused on modern. */
    public static Item dropItemFor(EntityType<?> type) {
        if (type == CRIMSON_BOAT) return ModItems.CRIMSON_BOAT;
        if (type == WARPED_BOAT) return ModItems.WARPED_BOAT;
        if (type == CRIMSON_CHEST_BOAT) return ModItems.CRIMSON_CHEST_BOAT;
        if (type == WARPED_CHEST_BOAT) return ModItems.WARPED_CHEST_BOAT;
        return null;
    }

    private static EntityType<Boat> registerBoat(String name, Supplier<Item> dropItem) {
        //[[[cog
        // compat.emit_register_boat_body(cog, loader, ver)
        //]]]
        var id = Platform.id(LavaBoats.MOD_ID, name);
        EntityType<Boat> type = EntityType.Builder
                .<Boat>of((t, level) -> new Boat(t, level, dropItem), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)
                .fireImmune()
                .build(ResourceKey.create(Registries.ENTITY_TYPE, id));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
        //[[[end]]]
    }

    private static EntityType<ChestBoat> registerChestBoat(String name, Supplier<Item> dropItem) {
        //[[[cog
        // compat.emit_register_chest_boat_body(cog, loader, ver)
        //]]]
        var id = Platform.id(LavaBoats.MOD_ID, name);
        EntityType<ChestBoat> type = EntityType.Builder
                .<ChestBoat>of((t, level) -> new ChestBoat(t, level, dropItem), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)
                .fireImmune()
                .build(ResourceKey.create(Registries.ENTITY_TYPE, id));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
        //[[[end]]]
    }
}
