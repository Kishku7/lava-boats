package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Server-authoritative rider safety: any living passenger of a lava boat is
 * kept un-burnt. Runs for every client (modded or vanilla) because the server
 * owns fire ticks and damage.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityFireSafetyMixin {

    @Inject(method = "baseTick", at = @At("TAIL"))
    private void lavaboats$extinguishInLavaBoat(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        Entity vehicle = self.getVehicle();
        if (vehicle != null && ModEntities.isLavaBoat(vehicle.getType()) && self.getRemainingFireTicks() > 0) {
            self.setRemainingFireTicks(0);
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void lavaboats$cancelFireDamage(ServerLevel level, DamageSource source, float amount,
                                            CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        Entity vehicle = self.getVehicle();
        if (vehicle != null && ModEntities.isLavaBoat(vehicle.getType()) && source.is(DamageTypeTags.IS_FIRE)) {
            cir.setReturnValue(false);
        }
    }
}
