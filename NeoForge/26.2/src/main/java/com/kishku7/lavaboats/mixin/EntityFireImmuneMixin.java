package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.world.entity.Entity;

/**
 * Rider fire-safety: while riding a lava boat, an entity reports {@code fireImmune() == true}.
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
