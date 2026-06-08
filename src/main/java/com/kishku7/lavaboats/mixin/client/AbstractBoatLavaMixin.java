package com.kishku7.lavaboats.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

import com.kishku7.lavaboats.ModEntities;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * Client-side lava buoyancy for the nether-stem boats.
 *
 * 1) The boat's water-detection methods ({@code checkInWater}, {@code isUnderwater},
 *    {@code getWaterLevelAbove}) only test {@link FluidTags#WATER}. For our boats we
 *    make those checks also accept lava, so the vanilla {@code floatBoat()} /
 *    {@code controlBoat()} logic treats lava like water (buoyancy + full speed).
 *
 * 2) A small buoyancy boost on lava. The hull bottom STAYS submerged (so buoyancy is
 *    stable - no bouncing), but the boat rides high enough that its interior floor and
 *    the rider sit above the lava surface. The boost must stay below the natural
 *    submersion (~0.366) or the equilibrium would rise above the surface, lose
 *    buoyancy, and oscillate.
 *
 * Client-only because the controlling client is authoritative for a ridden boat.
 */
@Mixin(AbstractBoat.class)
public abstract class AbstractBoatLavaMixin {

    /** Lava ride boost (blocks). Must stay < ~0.366 so the hull bottom remains submerged (stable). */
    private static final double LAVA_FLOAT_BOOST = 0.20;

    @Shadow
    private double waterLevel;

    @WrapOperation(
            method = {"checkInWater", "isUnderwater", "getWaterLevelAbove"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean lavaboats$treatLavaAsWater(FluidState state, TagKey<Fluid> tag, Operation<Boolean> original) {
        boolean isWater = original.call(state, tag);
        if (isWater) {
            return true;
        }
        AbstractBoat self = (AbstractBoat) (Object) this;
        if (FluidTags.WATER.equals(tag) && ModEntities.isLavaBoat(self.getType()) && state.is(FluidTags.LAVA)) {
            return true;
        }
        return false;
    }

    @Inject(method = "floatBoat", at = @At("HEAD"))
    private void lavaboats$raiseOnLava(CallbackInfo ci) {
        AbstractBoat self = (AbstractBoat) (Object) this;
        if (!ModEntities.isLavaBoat(self.getType())) {
            return;
        }
        // Stable gate: lava in the block at the hull base OR just below it. This stays
        // true across the whole float range, so there's no on/off force discontinuity
        // (which is what caused the earlier bouncing).
        BlockPos base = BlockPos.containing(self.getX(), self.getY(), self.getZ());
        if (self.level().getFluidState(base).is(FluidTags.LAVA)
                || self.level().getFluidState(base.below()).is(FluidTags.LAVA)) {
            this.waterLevel += LAVA_FLOAT_BOOST;
        }
    }
}
