package com.kishku7.lavaboats.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.kishku7.lavaboats.ModEntities;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * Client-side lava buoyancy. The boat's water-detection methods
 * ({@code checkInWater}, {@code isUnderwater}, {@code getWaterLevelAbove}) only
 * test {@link FluidTags#WATER}. For our nether-stem boats we make those checks
 * also accept lava, so the vanilla {@code floatBoat()} / {@code controlBoat()}
 * logic treats lava exactly like water: surface buoyancy + full water-speed.
 *
 * This is the controlling-client's authoritative simulation, which is why it
 * lives in a client-only mixin. Vanilla clients keep sinking, by design.
 */
@Mixin(AbstractBoat.class)
public abstract class AbstractBoatLavaMixin {

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
}
