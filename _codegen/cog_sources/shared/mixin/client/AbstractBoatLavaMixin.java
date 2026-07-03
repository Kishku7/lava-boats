// SHARED SOURCE -- canonical location: _codegen/cog_sources/shared. Pre-26 cell gen/ copies are
// materialized from here by scripts/cog-gen.ps1; the plain 26-shaped twin lives in shared_minecraft (keep in sync).
package com.kishku7.lavaboats.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_core
// cog.outl("import " + compat_core.boat_pkg(ver) + "." + compat_core.boat_base_type(ver) + ";")
//]]]
//[[[end]]]
import net.minecraft.world.phys.Vec3;

/** Loader-agnostic lava buoyancy: float boost + submersion recovery, keyed on isLavaBoat. */
//[[[cog
// cog.outl("@Mixin(" + compat_core.boat_base_type(ver) + ".class)")
//]]]
//[[[end]]]
public abstract class AbstractBoatLavaMixin {

    private static final double LAVA_FLOAT_BOOST = 0.20;
    private static final double LAVA_RESURFACE_SPEED = 0.10;

    @Shadow
    private double waterLevel;

    @Inject(method = "floatBoat", at = @At("HEAD"))
    private void lavaboats$raiseOnLava(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!ModEntities.isLavaBoat(self.getType())) {
            return;
        }
        BlockPos base = BlockPos.containing(self.getX(), self.getY(), self.getZ());
        if (self.level().getFluidState(base).is(FluidTags.LAVA)
                || self.level().getFluidState(base.below()).is(FluidTags.LAVA)) {
            this.waterLevel += LAVA_FLOAT_BOOST;
        }
    }

    // NeoForge deprecates the vanilla TagKey fluid helpers in favour of its FluidType API; the
    // vanilla methods are the only surface that compiles on Fabric AND Forge AND NeoForge (this
    // file is shared across all loaders), so keep them and suppress the NeoForge-only warning.
    @SuppressWarnings("deprecation")
    @Inject(method = "floatBoat", at = @At("TAIL"))
    private void lavaboats$bobUpFromLava(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!ModEntities.isLavaBoat(self.getType())) {
            return;
        }
        if (self.isEyeInFluid(FluidTags.LAVA)) {
            Vec3 m = self.getDeltaMovement();
            if (m.y < LAVA_RESURFACE_SPEED) {
                self.setDeltaMovement(m.x, LAVA_RESURFACE_SPEED, m.z);
            }
        }
    }
}
