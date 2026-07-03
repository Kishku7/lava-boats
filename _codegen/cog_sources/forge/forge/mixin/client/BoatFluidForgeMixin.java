package com.kishku7.lavaboats.forge.mixin.client;

//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_forge as compat
// compat.emit_boat_fluid_forge(cog, ver)
//]]]
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.level.material.FluidState;

/** Forge boat buoyancy: redirect canBoatInFluid so lava counts for our boats. */
@Mixin(AbstractBoat.class)
public abstract class BoatFluidForgeMixin {

    @Redirect(
            method = {"checkInWater", "isUnderwater", "getWaterLevelAbove"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;canBoatInFluid(Lnet/minecraft/world/level/material/FluidState;)Z",
                    remap = false)
    )
    private boolean lavaboats$lavaCountsForBoat(AbstractBoat self, FluidState state) {
        if (self.canBoatInFluid(state)) {
            return true;
        }
        return ModEntities.isLavaBoat(self.getType()) && state.is(FluidTags.LAVA);
    }
}
//[[[end]]]
