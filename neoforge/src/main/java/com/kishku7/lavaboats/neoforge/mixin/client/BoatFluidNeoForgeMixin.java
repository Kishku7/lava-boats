package com.kishku7.lavaboats.neoforge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.FluidState;

/**
 * NeoForge boat buoyancy: NeoForge patches the boat to test {@code this.canBoatInFluid(fluidState)}
 * (an IBoatExtension hook) instead of {@code FluidState.is(WATER)}. Redirect that call so lava also
 * counts for our lava boats while preserving the normal water behaviour for everything else.
 */
@Mixin(AbstractBoat.class)
public abstract class BoatFluidNeoForgeMixin {

    @Redirect(
            method = {"checkInWater", "isUnderwater", "getWaterLevelAbove"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;canBoatInFluid(Lnet/minecraft/world/level/material/FluidState;)Z",
                    remap = false)
    )
    private boolean lavaboats$lavaCountsForBoat(AbstractBoat self, FluidState state) {
        if (self.canBoatInFluid(state)) {
            return true;
        }
        return ModEntities.isLavaBoat(self.getType()) && state.is(FluidTags.LAVA);
    }
}
