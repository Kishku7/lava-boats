package com.kishku7.lavaboats.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;

/**
 * Common (loader-agnostic) half of the lava buoyancy. Runs on BOTH sides (1.1.2) so an unridden or
 * server-authoritative boat floats too. The other half — making the boat's water-detection treat
 * lava like water — is loader-specific (Fabric redirects {@code FluidState.is(WATER)}; Forge injects
 * {@code canBoatInFluid}). See the per-loader mixins.
 *
 * - {@code floatBoat} HEAD: a small stable boost so the rider rides above the lava surface (kept
 *   below the natural submersion ~0.366 so the hull stays submerged and doesn't bounce).
 * - {@code floatBoat} TAIL: submersion recovery. Vanilla's UNDER_WATER buoyancy is only 0.01/tick,
 *   too weak to recover after a knock (ghast fireball, explosion). Drive a firm steady rise while
 *   the eye is submerged in lava so the boat ALWAYS bobs back up; auto-stops at the surface.
 */
@Mixin(Boat.class)
public abstract class AbstractBoatLavaMixin {

    private static final double LAVA_FLOAT_BOOST = 0.20;
    private static final double LAVA_RESURFACE_SPEED = 0.10;

    @Shadow
    private double waterLevel;

    @Inject(method = "floatBoat", at = @At("HEAD"))
    private void lavaboats$raiseOnLava(CallbackInfo ci) {
        Boat self = (Boat) (Object) this;
        if (!ModEntities.isLavaBoat(self.getType())) {
            return;
        }
        BlockPos base = BlockPos.containing(self.getX(), self.getY(), self.getZ());
        if (self.level().getFluidState(base).is(FluidTags.LAVA)
                || self.level().getFluidState(base.below()).is(FluidTags.LAVA)) {
            this.waterLevel += LAVA_FLOAT_BOOST;
        }
    }

    @Inject(method = "floatBoat", at = @At("TAIL"))
    private void lavaboats$bobUpFromLava(CallbackInfo ci) {
        Boat self = (Boat) (Object) this;
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
