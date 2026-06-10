package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.world.entity.Entity;

/**
 * Rider fire-safety. While an entity is riding one of our lava boats it reports
 * {@code fireImmune() == true}. Because vanilla gates lava ignition, lava damage,
 * AND {@code isOnFire()} on {@code !fireImmune()}, this single hook means the rider
 * takes no fire/lava damage, is never ignited, and shows no flame overlay.
 *
 * Server-authoritative for the damage side and applies client-side for the visual,
 * so it lives in the common mixin set (applies on both). Uses only vanilla Mixin
 * injectors (no MixinExtras) so no extra runtime library is required on any loader.
 */
@Mixin(Entity.class)
public abstract class EntityFireImmuneMixin {

    @Inject(method = "fireImmune", at = @At("RETURN"), cancellable = true)
    private void lavaboats$fireImmuneWhileRidingLavaBoat(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            return; // already immune
        }
        Entity self = (Entity) (Object) this;
        Entity vehicle = self.getVehicle();
        if (vehicle != null && ModEntities.isLavaBoat(vehicle.getType())) {
            cir.setReturnValue(true);
        }
    }
}
