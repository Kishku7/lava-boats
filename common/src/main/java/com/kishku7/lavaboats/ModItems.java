package com.kishku7.lavaboats;

import java.util.List;
import java.util.function.Supplier;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(LavaBoats.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> CRIMSON_BOAT = register("crimson_boat", ModEntities.CRIMSON_BOAT);
    public static final RegistrySupplier<Item> CRIMSON_CHEST_BOAT = register("crimson_chest_boat", ModEntities.CRIMSON_CHEST_BOAT);
    public static final RegistrySupplier<Item> WARPED_BOAT = register("warped_boat", ModEntities.WARPED_BOAT);
    public static final RegistrySupplier<Item> WARPED_CHEST_BOAT = register("warped_chest_boat", ModEntities.WARPED_CHEST_BOAT);

    /** Creative-tab insertion order (Tools &amp; Utilities), wired per-loader. */
    public static final List<RegistrySupplier<Item>> TAB_ITEMS =
            List.of(CRIMSON_BOAT, CRIMSON_CHEST_BOAT, WARPED_BOAT, WARPED_CHEST_BOAT);

    public static void init() {
        ITEMS.register();
    }

    public static boolean isLavaBoatItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item i = stack.getItem();
        return i == CRIMSON_BOAT.get() || i == CRIMSON_CHEST_BOAT.get()
                || i == WARPED_BOAT.get() || i == WARPED_CHEST_BOAT.get();
    }

    private static RegistrySupplier<Item> register(String name, Supplier<? extends EntityType<? extends AbstractBoat>> type) {
        Identifier id = Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        return ITEMS.register(name, () -> new BoatItem(type.get(), new Item.Properties().stacksTo(1).fireResistant().setId(key)));
    }
}
