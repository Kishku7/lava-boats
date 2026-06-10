package com.kishku7.lavaboats.entity;

import java.util.function.Supplier;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * A plain boat whose drop item is supplied by the mod (1.20.x {@link Boat} has no drop-item
 * constructor parameter; the vanilla drop is keyed off {@link Boat.Type}, so we override
 * {@link #getDropItem()} instead). Geometry and physics are vanilla boat; the lava behaviour
 * comes from the mixins keyed on the entity type.
 */
public class LavaBoat extends Boat {
    private final Supplier<Item> dropItem;

    public LavaBoat(EntityType<? extends Boat> type, Level level, Supplier<Item> dropItem) {
        super(type, level);
        this.dropItem = dropItem;
    }

    @Override
    public Item getDropItem() {
        return this.dropItem.get();
    }
}
