package com.kishku7.lavaboats.item;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Spawns one of our custom lava-boat entities. Vanilla {@link net.minecraft.world.item.BoatItem}
 * in 1.20.x hard-codes vanilla {@link Boat} creation by {@link Boat.Type}, so it can't place a
 * custom entity type; this faithfully mirrors its raytrace-and-place {@code use()} but spawns the
 * supplied entity type instead.
 */
public class LavaBoatItem extends Item {
    private final Supplier<? extends EntityType<? extends Boat>> type;

    public LavaBoatItem(Supplier<? extends EntityType<? extends Boat>> type, Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        }

        Vec3 view = player.getViewVector(1.0F);
        List<Entity> nearby = level.getEntities(player,
                player.getBoundingBox().expandTowards(view.scale(5.0)).inflate(1.0),
                EntitySelector.NO_SPECTATORS);
        if (!nearby.isEmpty()) {
            Vec3 eye = player.getEyePosition();
            for (Entity e : nearby) {
                AABB box = e.getBoundingBox().inflate(e.getPickRadius());
                if (box.contains(eye)) {
                    return InteractionResultHolder.pass(stack);
                }
            }
        }

        if (hit.getType() == HitResult.Type.BLOCK) {
            Boat boat = this.type.get().create(level);
            if (boat == null) {
                return InteractionResultHolder.pass(stack);
            }
            Vec3 loc = hit.getLocation();
            boat.setPos(loc.x, loc.y, loc.z);
            boat.setYRot(player.getYRot());
            if (!level.noCollision(boat, boat.getBoundingBox())) {
                return InteractionResultHolder.fail(stack);
            }
            if (!level.isClientSide) {
                level.addFreshEntity(boat);
                level.gameEvent(player, GameEvent.ENTITY_PLACE, hit.getLocation());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.pass(stack);
    }
}
