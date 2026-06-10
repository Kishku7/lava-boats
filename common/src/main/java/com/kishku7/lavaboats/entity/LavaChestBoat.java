package com.kishku7.lavaboats.entity;

import java.util.function.Supplier;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Chest variant of {@link LavaBoat}. Extends vanilla {@link ChestBoat} for the container
 * behaviour and overrides the drop item.
 */
public class LavaChestBoat extends ChestBoat {
    private final Supplier<Item> dropItem;

    public LavaChestBoat(EntityType<? extends ChestBoat> type, Level level, Supplier<Item> dropItem) {
        super(type, level);
        this.dropItem = dropItem;
    }

    @Override
    public Item getDropItem() {
        return this.dropItem.get();
    }
}
