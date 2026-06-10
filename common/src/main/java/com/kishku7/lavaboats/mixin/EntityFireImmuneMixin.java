package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.world.entity.Entity;

/**
 * Rider fire-safety. While an entity is riding one of our lava boats it reports
 * {@code fireImmune() == true} — no fire/lava damage, never ignited, no flame overlay.
 * Vanilla Mixin injectors only (no MixinExtras) so no runtime lib is required on any loader.
 */
@Mixin(Entity.class)
public abstract class EntityFireImmuneMixin {

    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    private void lavaboats$fireImmuneWhileRidingLavaBoat(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return;
        }
        Entity self = (Entity) (Object) this;
        Entity vehicle = self.getVehicle();
        if (vehicle != null && ModEntities.isLavaBoat(vehicle.getType())) {
            cir.setReturnValue(true);
        }
    }
}
