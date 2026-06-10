package com.kishku7.lavaboats.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * Fabric/vanilla boat buoyancy: the water-detection methods test {@code FluidState.is(WATER)}.
 * Redirect that so lava also counts for our lava boats.
 */
@Mixin(AbstractBoat.class)
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
        AbstractBoat self = (AbstractBoat) (Object) this;
        return FluidTags.WATER.equals(tag) && ModEntities.isLavaBoat(self.getType()) && state.is(FluidTags.LAVA);
    }
}
