package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;

/** Legacy (pre-1.21.2) drop override. Vanilla Boat.getDropItem() returns an oak boat by Boat.Type;
 *  for our lava boats return the matching mod item. Replaces the old LavaBoat/LavaChestBoat subclasses. */
@Mixin(Boat.class)
public abstract class BoatDropMixin {
    @Inject(method = "getDropItem", at = @At("HEAD"), cancellable = true)
    private void lavaboats$dropOurItem(CallbackInfoReturnable<Item> cir) {
        Entity self = (Entity) (Object) this;
        Item drop = ModEntities.dropItemFor(self.getType());
        if (drop != null) {
            cir.setReturnValue(drop);
        }
    }
}
