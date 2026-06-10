package com.kishku7.lavaboats;

import java.util.List;
import java.util.function.Supplier;

import com.kishku7.lavaboats.item.LavaBoatItem;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
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

    /** True for a dropped/held lava-boat item. Used by the item lava-float mixin. */
    public static boolean isLavaBoatItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item i = stack.getItem();
        return i == CRIMSON_BOAT.get() || i == CRIMSON_CHEST_BOAT.get()
                || i == WARPED_BOAT.get() || i == WARPED_CHEST_BOAT.get();
    }

    private static RegistrySupplier<Item> register(String name, Supplier<? extends EntityType<? extends Boat>> type) {
        // fireResistant() = the Netherite mechanism: the dropped item survives fire & lava.
        return ITEMS.register(name, () -> new LavaBoatItem(type, new Item.Properties().stacksTo(1).fireResistant()));
    }
}
