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
 * 2) Lava boats float a little higher than the default water equilibrium so the hull
 *    rests on the lava surface and the rider's body stays out of the lava (no flames,
 *    no lava filling the boat). Done by nudging {@code waterLevel} up in {@code floatBoat}
 *    while the boat is over lava.
 *
 * Client-only because the controlling client is authoritative for a ridden boat.
 */
@Mixin(AbstractBoat.class)
public abstract class AbstractBoatLavaMixin {

    /** How far above the water-equilibrium a lava boat rides (blocks). Keeps the rider clear of lava. */
    private static final double LAVA_FLOAT_BOOST = 0.42;

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
        // Only boost while sitting over lava (block at the hull base is lava). This is
        // stable across the float equilibrium, so it won't oscillate like an isInLava() gate.
        BlockPos base = BlockPos.containing(self.getX(), self.getY(), self.getZ());
        if (self.level().getFluidState(base).is(FluidTags.LAVA)) {
            this.waterLevel += LAVA_FLOAT_BOOST;
        }
    }
}
