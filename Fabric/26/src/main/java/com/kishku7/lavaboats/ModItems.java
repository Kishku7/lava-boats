package com.kishku7.lavaboats;

import java.util.Set;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ModItems {
    private ModItems() {}

    public static Item CRIMSON_BOAT;
    public static Item WARPED_BOAT;
    public static Item CRIMSON_CHEST_BOAT;
    public static Item WARPED_CHEST_BOAT;

    private static Set<Item> LAVA_BOAT_ITEMS = Set.of();

    public static void register() {
        CRIMSON_BOAT = registerBoatItem("crimson_boat", ModEntities.CRIMSON_BOAT);
        CRIMSON_CHEST_BOAT = registerBoatItem("crimson_chest_boat", ModEntities.CRIMSON_CHEST_BOAT);
        WARPED_BOAT = registerBoatItem("warped_boat", ModEntities.WARPED_BOAT);
        WARPED_CHEST_BOAT = registerBoatItem("warped_chest_boat", ModEntities.WARPED_CHEST_BOAT);

        LAVA_BOAT_ITEMS = Set.of(CRIMSON_BOAT, WARPED_BOAT, CRIMSON_CHEST_BOAT, WARPED_CHEST_BOAT);

        // Slot the boats into the Tools & Utilities tab alongside the vanilla boats.
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {
            output.accept(CRIMSON_BOAT);
            output.accept(CRIMSON_CHEST_BOAT);
            output.accept(WARPED_BOAT);
            output.accept(WARPED_CHEST_BOAT);
        });
    }

    /** True for a dropped/held lava-boat item. Used by the item lava-float mixin. */
    public static boolean isLavaBoatItem(ItemStack stack) {
        return !stack.isEmpty() && LAVA_BOAT_ITEMS.contains(stack.getItem());
    }

    private static Item registerBoatItem(String name, EntityType<? extends AbstractBoat> type) {
        Identifier id = Identifier.fromNamespaceAndPath(LavaBoats.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        // fireResistant() = the Netherite mechanism: the dropped item survives fire & lava.
        Item item = new BoatItem(type, new Item.Properties().stacksTo(1).fireResistant().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }
}
