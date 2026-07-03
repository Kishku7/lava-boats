package com.kishku7.lavaboats.fabric.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_fabric as compat
// cog.outl("import " + compat.boat_pkg(ver) + "." + compat.boat_base_type(ver) + ";")
//]]]
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
//[[[end]]]
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/** Buoyancy detection: redirect FluidState.is(WATER) so lava counts for our boats. */
//[[[cog
// cog.outl("@Mixin(" + compat.boat_base_type(ver) + ".class)")
//]]]
@Mixin(AbstractBoat.class)
//[[[end]]]
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
        Entity self = (Entity) (Object) this;
        return FluidTags.WATER.equals(tag) && ModEntities.isLavaBoat(self.getType()) && state.is(FluidTags.LAVA);
    }
}
