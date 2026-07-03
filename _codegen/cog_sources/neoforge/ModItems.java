package com.kishku7.lavaboats;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_neoforge as compat
// cog.outl("import net.minecraft.resources." + compat.id_type(ver) + ";")
// cog.outl("import " + compat.boat_pkg(ver) + "." + compat.boat_base_type(ver) + ";")
// if compat.is_legacy(ver): cog.outl("import com.kishku7.lavaboats.item.LavaBoatItem;")
//]]]
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
//[[[end]]]
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

/** NeoForge item registration (DeferredRegister/DeferredHolder). Vanilla BoatItem (modern) or legacy LavaBoatItem. */
public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, LavaBoats.MOD_ID);

    public static final DeferredHolder<Item, Item> CRIMSON_BOAT = register("crimson_boat", () -> ModEntities.CRIMSON_BOAT.get());
    public static final DeferredHolder<Item, Item> CRIMSON_CHEST_BOAT = register("crimson_chest_boat", () -> ModEntities.CRIMSON_CHEST_BOAT.get());
    public static final DeferredHolder<Item, Item> WARPED_BOAT = register("warped_boat", () -> ModEntities.WARPED_BOAT.get());
    public static final DeferredHolder<Item, Item> WARPED_CHEST_BOAT = register("warped_chest_boat", () -> ModEntities.WARPED_CHEST_BOAT.get());

    public static final List<DeferredHolder<Item, Item>> TAB_ITEMS =
            List.of(CRIMSON_BOAT, CRIMSON_CHEST_BOAT, WARPED_BOAT, WARPED_CHEST_BOAT);

    //[[[cog
    // compat.emit_items_register_bus(cog, ver)
    //]]]
    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
    //[[[end]]]

    public static boolean isLavaBoatItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item i = stack.getItem();
        return i == CRIMSON_BOAT.get() || i == CRIMSON_CHEST_BOAT.get()
                || i == WARPED_BOAT.get() || i == WARPED_CHEST_BOAT.get();
    }

    //[[[cog
    // compat.emit_register_item_method(cog, ver)
    //]]]
    private static DeferredHolder<Item, Item> register(String name, Supplier<? extends EntityType<? extends AbstractBoat>> type) {
        Identifier id = Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return ITEMS.register(name, () -> new BoatItem(type.get(), new Item.Properties().stacksTo(1).fireResistant().setId(key)));
    }
    //[[[end]]]
}
