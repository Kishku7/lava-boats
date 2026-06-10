package com.kishku7.lavaboats.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * Fabric/vanilla buoyancy detection. Vanilla {@code Boat#checkInWater}/{@code isUnderwater}/
 * {@code getWaterLevelAbove} test {@code FluidState.is(FluidTags.WATER)} directly; for our boats we
 * redirect that to also accept lava, so the vanilla float logic treats lava like water.
 *
 * Fabric-only: Forge patches these methods to use {@code canBoatInFluid} instead (see the Forge mixin).
 */
@Mixin(Boat.class)
public abstract class BoatWaterFabricMixin {

    @Redirect(
            method = {"checkInWater", "isUnderwater", "getWaterLevelAbove"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/FluidState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean lavaboats$treatLavaAsWater(FluidState state, TagKey<Fluid> tag) {
        if (state.is(tag)) {
            return true;
        }
        Boat self = (Boat) (Object) this;
        return FluidTags.WATER.equals(tag) && ModEntities.isLavaBoat(self.getType()) && state.is(FluidTags.LAVA);
    }
}
