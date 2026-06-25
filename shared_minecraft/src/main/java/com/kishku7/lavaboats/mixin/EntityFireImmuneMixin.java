package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.kishku7.lavaboats.ModEntities;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.entity.Entity;

/**
 * Rider fire-safety. While an entity is riding one of our lava boats it reports
 * {@code fireImmune() == true}. Because vanilla gates lava ignition, lava damage,
 * AND {@code isOnFire()} on {@code !fireImmune()}, this single hook means the rider
 * takes no fire/lava damage, is never ignited, and shows no flame overlay.
 *
 * Server-authoritative for the damage side and applies client-side for the visual,
 * so it lives in the common mixin set (applies on both).
 */
@Mixin(Entity.class)
public abstract class EntityFireImmuneMixin {

    @ModifyReturnValue(method = "fireImmune", at = @At("RETURN"))
    private boolean lavaboats$fireImmuneWhileRidingLavaBoat(boolean original) {
        if (original) {
            return true;
        }
        Entity self = (Entity) (Object) this;
        Entity vehicle = self.getVehicle();
        return vehicle != null && ModEntities.isLavaBoat(vehicle.getType());
    }
}
