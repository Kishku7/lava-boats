package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Depth Strider in lava. Depth Strider grants the {@link Attributes#WATER_MOVEMENT_EFFICIENCY}
 * attribute, which {@code travelInWater} uses to cut slowdown and raise speed. {@code travelInLava}
 * ignores it; these hooks make it honour the same attribute, interpolating the base speed and the
 * horizontal drag by it exactly like the water path. No effect when the attribute is 0 (no Depth
 * Strider); the 3 enchant levels scale via the attribute.
 */
@Mixin(LivingEntity.class)
public abstract class LavaDepthStriderMixin {

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
