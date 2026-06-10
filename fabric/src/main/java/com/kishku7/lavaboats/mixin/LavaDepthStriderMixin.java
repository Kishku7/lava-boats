package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Depth Strider in lava.
 *
 * At 26.x, Depth Strider no longer uses the old getDepthStrider() formula — it grants the
 * {@link Attributes#WATER_MOVEMENT_EFFICIENCY} attribute, and {@code travelInWater} uses that
 * attribute to cut the slowdown and raise the player's speed (which is why higher levels feel
 * faster). {@code travelInLava} is a separate path that uses a fixed 0.02 base speed and a flat
 * x0.5 drag and ignores the attribute entirely.
 *
 * These two hooks make {@code travelInLava} honour that same attribute, interpolating the base
 * speed (0.02 -> walk speed) and the horizontal drag (0.5 -> 0.546, the water value) by the
 * attribute — exactly how {@code travelInWater} does it. With no Depth Strider the attribute is 0
 * and both hooks return the original constant unchanged, so ordinary lava movement is untouched.
 * The three enchantment levels need no special-casing: they're already baked into the attribute.
 */
@Mixin(LivingEntity.class)
public abstract class LavaDepthStriderMixin {

    /** The water-path target for the horizontal drag at full efficiency. */
    private static final double WATER_DRAG_TARGET = 0.54600006;

    private float lavaboats$lavaWalker() {
        LivingEntity self = (LivingEntity) (Object) this;
        float w = (float) self.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
        if (w <= 0.0F) {
            return 0.0F;
        }
        if (!self.onGround()) {
            w *= 0.5F;
        }
        return w;
    }

    @ModifyConstant(method = "travelInLava", constant = @Constant(floatValue = 0.02F))
    private float lavaboats$depthStriderLavaSpeed(float speed) {
        float w = lavaboats$lavaWalker();
        if (w <= 0.0F) {
            return speed;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        return speed + (self.getSpeed() - speed) * w;
    }

    @ModifyConstant(method = "travelInLava", constant = @Constant(doubleValue = 0.5))
    private double lavaboats$depthStriderLavaDrag(double drag) {
        float w = lavaboats$lavaWalker();
        if (w <= 0.0F) {
            return drag;
        }
        return drag + (WATER_DRAG_TARGET - drag) * w;
    }
}
