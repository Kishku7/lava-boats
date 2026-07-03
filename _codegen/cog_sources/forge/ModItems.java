package com.kishku7.lavaboats;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_forge as compat
// cog.outl("import " + compat.bus_import(ver) + ";")
// cog.outl("import net.minecraft.world.entity.vehicle." + compat.boat_base_type(ver) + ";")
// if compat.is_legacy(ver): cog.outl("import com.kishku7.lavaboats.item.LavaBoatItem;")
//]]]
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraft.world.entity.vehicle.AbstractBoat;
//[[[end]]]
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Forge item registration (DeferredRegister). Vanilla BoatItem (modern) or legacy placer LavaBoatItem. */
public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, LavaBoats.MOD_ID);

    public static final RegistryObject<Item> CRIMSON_BOAT = register("crimson_boat", () -> ModEntities.CRIMSON_BOAT.get());
    public static final RegistryObject<Item> CRIMSON_CHEST_BOAT = register("crimson_chest_boat", () -> ModEntities.CRIMSON_CHEST_BOAT.get());
    public static final RegistryObject<Item> WARPED_BOAT = register("warped_boat", () -> ModEntities.WARPED_BOAT.get());
    public static final RegistryObject<Item> WARPED_CHEST_BOAT = register("warped_chest_boat", () -> ModEntities.WARPED_CHEST_BOAT.get());

    public static final List<RegistryObject<Item>> TAB_ITEMS =
            List.of(CRIMSON_BOAT, CRIMSON_CHEST_BOAT, WARPED_BOAT, WARPED_CHEST_BOAT);

    //[[[cog
    // compat.emit_items_register_bus(cog, ver)
    //]]]
    public static void register(BusGroup modBus) {
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
    // base = compat.boat_base_type(ver)
    // cog.outl("private static RegistryObject<Item> register(String name, Supplier<? extends EntityType<? extends " + base + ">> type) {")
    // compat.emit_register_item_body(cog, ver)
    // cog.outl("}")
    //]]]
    private static RegistryObject<Item> register(String name, Supplier<? extends EntityType<? extends AbstractBoat>> type) {
    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(LavaBoats.MOD_ID, name);
    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
    return ITEMS.register(name, () -> new BoatItem(type.get(), new Item.Properties().stacksTo(1).fireResistant().setId(key)));
    }
    //[[[end]]]
}
