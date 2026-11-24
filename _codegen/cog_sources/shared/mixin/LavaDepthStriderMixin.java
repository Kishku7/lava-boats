// SHARED SOURCE -- canonical location: _codegen/cog_sources/shared. Pre-26 cell gen/ copies are
// materialized from here by scripts/cog-gen.ps1; the plain 26-shaped twin lives in shared_minecraft (keep in sync).
package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.material.FlowingFluid;

/**
 * Depth Strider in lava. Depth Strider grants the {@link Attributes#WATER_MOVEMENT_EFFICIENCY}
 * attribute, which {@code travelInWater} uses to cut slowdown and raise speed. {@code travelInLava}
 * ignores it; these hooks make it honour the same attribute, interpolating the base speed and the
 * horizontal drag by it exactly like the water path. No effect when the attribute is 0 (no Depth
 * Strider), so ordinary lava movement is untouched; the 3 enchant levels scale via the attribute.
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

    //[[[cog
    // import cog
    // v = tuple(int(x) for x in ver.split("-")[0].split("."))
    // if v >= (26, 4):
    //     # 26.4 moved lava's horizontal drag out of travelInLava's 0.5 literal into Fluids.LAVA.getDrag().
    //     cog.outl('    @Redirect(method = "travelInLava", at = @At(value = "INVOKE",')
    //     cog.outl('            target = "Lnet/minecraft/world/level/material/FlowingFluid;getDrag()F"))')
    //     cog.outl('    private float lavaboats$depthStriderLavaDrag(FlowingFluid fluid) {')
    //     cog.outl('        float drag = fluid.getDrag();')
    //     cog.outl('        float w = lavaboats$lavaWalker();')
    //     cog.outl('        if (w <= 0.0F) {')
    //     cog.outl('            return drag;')
    //     cog.outl('        }')
    //     cog.outl('        return drag + ((float) WATER_DRAG_TARGET - drag) * w;')
    //     cog.outl('    }')
    // else:
    //     cog.outl('    @ModifyConstant(method = "travelInLava", constant = @Constant(doubleValue = 0.5))')
    //     cog.outl('    private double lavaboats$depthStriderLavaDrag(double drag) {')
    //     cog.outl('        float w = lavaboats$lavaWalker();')
    //     cog.outl('        if (w <= 0.0F) {')
    //     cog.outl('            return drag;')
    //     cog.outl('        }')
    //     cog.outl('        return drag + (WATER_DRAG_TARGET - drag) * w;')
    //     cog.outl('    }')
    //]]]
    @ModifyConstant(method = "travelInLava", constant = @Constant(doubleValue = 0.5))
    private double lavaboats$depthStriderLavaDrag(double drag) {
        float w = lavaboats$lavaWalker();
        if (w <= 0.0F) {
            return drag;
        }
        return drag + (WATER_DRAG_TARGET - drag) * w;
    }
    //[[[end]]]
}
