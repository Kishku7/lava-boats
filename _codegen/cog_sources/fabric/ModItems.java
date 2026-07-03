package com.kishku7.lavaboats;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_fabric as compat
// import compat_core; compat_core.emit_id_type_import(cog, loader, ver)
// cog.outl("import " + compat.boat_pkg(ver) + "." + compat.boat_base_type(ver) + ";")
// if compat.is_legacy(ver): cog.outl("import com.kishku7.lavaboats.item.LavaBoatItem;")
//]]]
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
//[[[end]]]
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Item registration. Vanilla BoatItem (>=1.21.2) or the legacy placer LavaBoatItem (pre-1.21.2). */
public final class ModItems {
    private ModItems() {}

    public static Item CRIMSON_BOAT;
    public static Item CRIMSON_CHEST_BOAT;
    public static Item WARPED_BOAT;
    public static Item WARPED_CHEST_BOAT;

    public static List<Item> TAB_ITEMS = List.of();

    public static void init() {
        CRIMSON_BOAT = register("crimson_boat", () -> ModEntities.CRIMSON_BOAT);
        CRIMSON_CHEST_BOAT = register("crimson_chest_boat", () -> ModEntities.CRIMSON_CHEST_BOAT);
        WARPED_BOAT = register("warped_boat", () -> ModEntities.WARPED_BOAT);
        WARPED_CHEST_BOAT = register("warped_chest_boat", () -> ModEntities.WARPED_CHEST_BOAT);
        TAB_ITEMS = List.of(CRIMSON_BOAT, CRIMSON_CHEST_BOAT, WARPED_BOAT, WARPED_CHEST_BOAT);
    }

    public static boolean isLavaBoatItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item i = stack.getItem();
        return i == CRIMSON_BOAT || i == CRIMSON_CHEST_BOAT
                || i == WARPED_BOAT || i == WARPED_CHEST_BOAT;
    }

    //[[[cog
    // compat.emit_register_item_method(cog, loader, ver)
    //]]]
    private static Item register(String name, Supplier<? extends EntityType<? extends AbstractBoat>> type) {
        var id = Platform.id(LavaBoats.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        BoatItem item = new BoatItem(type.get(), new Item.Properties().stacksTo(1).fireResistant().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }
    //[[[end]]]
}
