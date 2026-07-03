package com.kishku7.lavaboats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kishku7.lavaboats.ModItems;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Dropped lava-boat items float up to the lava surface quickly (vanilla only nudges
 * submerged items up by 0.0005/tick, which is imperceptible). The push scales with how
 * deep the item is in the lava, so it rises fast when submerged and settles gently at
 * the top. Runs inside vanilla's {@code setUnderLavaMovement}, which is only called when
 * the item is genuinely in lava, so it auto-stops at the surface.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityLavaFloatMixin {

    // NeoForge deprecates the vanilla TagKey fluid helpers in favour of its FluidType API; the
    // vanilla methods are the only surface that compiles on Fabric AND Forge AND NeoForge (this
    // file is shared across all loaders), so keep them and suppress the NeoForge-only warning.
    @SuppressWarnings("deprecation")
    @Inject(method = "setUnderLavaMovement", at = @At("TAIL"))
    private void lavaboats$boatItemFloatsUp(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!ModItems.isLavaBoatItem(self.getItem())) {
            return;
        }
        double depth = self.getFluidHeight(FluidTags.LAVA);
        double target = Math.min(0.03 + depth * 0.14, 0.14); // up to ~0.14/tick rise
        Vec3 m = self.getDeltaMovement();
        self.setDeltaMovement(m.x * 0.95, Math.min(m.y + target, target), m.z * 0.95);
    }
}
