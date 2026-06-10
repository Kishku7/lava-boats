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
import net.minecraft.world.phys.Vec3;

/**
 * Lava buoyancy for the nether-stem boats. Runs on BOTH sides (1.1.2): a ridden boat is
 * client-authoritative, but an unridden boat (or a passenger-sync hiccup) is server-driven, so the
 * server must float lava boats too or they sink to the lava floor.
 *
 * 1) The boat's water-detection methods ({@code checkInWater}, {@code isUnderwater},
 *    {@code getWaterLevelAbove}) only test {@link FluidTags#WATER}. For our boats we make those
 *    checks also accept lava, so the vanilla {@code floatBoat()} / {@code controlBoat()} logic
 *    treats lava like water (buoyancy + full speed).
 *
 * 2) A small buoyancy boost on lava so the rider rides clear of the surface (gated on lava at/below
 *    the hull base, stable across the whole float range so it doesn't bounce).
 *
 * 3) Submersion recovery: vanilla's UNDER_WATER buoyancy is only 0.01/tick, far too weak to bring a
 *    boat back up after it's knocked deep (ghast fireball, explosion, knockback). For lava boats we
 *    drive a firm steady rise whenever the eye is submerged in lava, so they ALWAYS bob back to the
 *    surface. It stops on its own the moment the eye clears the lava, so there's no overshoot loop.
 */
@Mixin(AbstractBoat.class)
public abstract class AbstractBoatLavaMixin {

    /** Lava ride boost (blocks). Must stay < ~0.366 so the hull bottom remains submerged (stable). */
    private static final double LAVA_FLOAT_BOOST = 0.20;

    /** Upward velocity (blocks/tick) used to recover a submerged lava boat back to the surface. */
    private static final double LAVA_RESURFACE_SPEED = 0.10;

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
        // Stable gate: lava in the block at the hull base OR just below it. This stays true across
        // the whole float range, so there's no on/off force discontinuity (which caused bouncing).
        BlockPos base = BlockPos.containing(self.getX(), self.getY(), self.getZ());
        if (self.level().getFluidState(base).is(FluidTags.LAVA)
                || self.level().getFluidState(base.below()).is(FluidTags.LAVA)) {
            this.waterLevel += LAVA_FLOAT_BOOST;
        }
    }

    @Inject(method = "floatBoat", at = @At("TAIL"))
    private void lavaboats$bobUpFromLava(CallbackInfo ci) {
        AbstractBoat self = (AbstractBoat) (Object) this;
        if (!ModEntities.isLavaBoat(self.getType())) {
            return;
        }
        // Knocked under the surface: vanilla's UNDER_WATER buoyancy (0.01/tick) can't recover.
        // Drive a firm, steady rise while the eye is submerged in lava; it auto-stops at the surface.
        if (self.isEyeInFluid(FluidTags.LAVA)) {
            Vec3 m = self.getDeltaMovement();
            if (m.y < LAVA_RESURFACE_SPEED) {
                self.setDeltaMovement(m.x, LAVA_RESURFACE_SPEED, m.z);
            }
        }
    }
}
