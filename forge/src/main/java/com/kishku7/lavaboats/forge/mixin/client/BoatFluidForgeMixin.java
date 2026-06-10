package com.kishku7.lavaboats.forge.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.FluidState;

/**
 * Forge buoyancy detection. Forge patches the boat's water checks to route through
 * {@code canBoatInFluid(FluidState)} — a default method on {@code IForgeBoat}, invoked via the
 * {@code Boat} class ({@code invokevirtual Boat.canBoatInFluid}) inside vanilla
 * {@code checkInWater}/{@code isUnderwater}/{@code getWaterLevelAbove}. We redirect that call site:
 * keep the original result, but also accept lava for our boats. Forge equivalent of the Fabric
 * {@code FluidState.is} redirect.
 *
 * Method names are vanilla (remapped); the {@code canBoatInFluid} target is a Forge method with a
 * stable name + vanilla-typed owner/args, so {@code remap = false}. Forge-only, client side.
 */
@Mixin(Boat.class)
public abstract class BoatFluidForgeMixin {

    @Redirect(
            method = {"checkInWater", "isUnderwater", "getWaterLevelAbove"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/vehicle/Boat;canBoatInFluid(Lnet/minecraft/world/level/material/FluidState;)Z",
                    remap = false)
    )
    private boolean lavaboats$lavaCountsAsRidable(Boat instance, FluidState state) {
        if (instance.canBoatInFluid(state)) {
            return true;
        }
        return ModEntities.isLavaBoat(instance.getType()) && state.is(FluidTags.LAVA);
    }
}
