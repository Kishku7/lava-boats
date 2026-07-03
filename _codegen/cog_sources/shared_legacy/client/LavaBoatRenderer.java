// SHARED SOURCE -- canonical location: _codegen/cog_sources/shared_legacy. Cell gen/ copies are
// materialized from here by scripts/cog-gen.ps1 (legacy era only); edit ONLY this copy.
package com.kishku7.lavaboats.client;

import com.kishku7.lavaboats.LavaBoats;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_core
// compat_core.emit_environment_imports(cog, loader)
//]]]
//[[[end]]]
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.joml.Quaternionf;

/**
 * Renders a lava boat with our custom texture and VANILLA boat geometry (pre-1.21.2 OLD-boat
 * model). Vanilla's private {@code BoatRenderer#getModelWithLocation} can't be intercepted
 * cross-loader, so we extend {@link EntityRenderer} directly and reproduce vanilla
 * {@code BoatRenderer.render} with our own fixed texture + model. Era drift handled by Cog:
 * ResourceLocation ctor vs factory (1.21), and the (r,g,b,a) float renderToBuffer overload vs
 * the packed-ARGB int form (1.21).
 */
//[[[cog
// compat_core.emit_environment(cog, loader)
//]]]
//[[[end]]]
public class LavaBoatRenderer extends EntityRenderer<Boat> {

    //[[[cog
    // for n, p in [("CRIMSON", "boat/crimson"), ("WARPED", "boat/warped"),
    //              ("CRIMSON_CHEST", "chest_boat/crimson"), ("WARPED_CHEST", "chest_boat/warped")]:
    //     cog.outl("public static final ResourceLocation {0} =".format(n))
    //     cog.outl("        {0};".format(compat_core.make_id(ver, "LavaBoats.MOD_ID", '"textures/entity/{0}.png"'.format(p), loader)))
    //]]]
    //[[[end]]]

    private final ResourceLocation texture;
    private final ListModel<Boat> model;

    public LavaBoatRenderer(EntityRendererProvider.Context ctx, ResourceLocation texture, boolean chest) {
        super(ctx);
        this.shadowRadius = 0.8F;
        this.texture = texture;
        ModelPart part = ctx.bakeLayer(chest
                ? ModelLayers.createChestBoatModelName(Boat.Type.OAK)
                : ModelLayers.createBoatModelName(Boat.Type.OAK));
        this.model = chest ? new ChestBoatModel(part) : new BoatModel(part);
    }

    @Override
    public void render(Boat boat, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        float hurt = (float) boat.getHurtTime() - partialTicks;
        float damage = boat.getDamage() - partialTicks;
        if (damage < 0.0F) {
            damage = 0.0F;
        }
        if (hurt > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurt) * hurt * damage / 10.0F * (float) boat.getHurtDir()));
        }
        float bubble = boat.getBubbleAngle(partialTicks);
        if (!Mth.equal(bubble, 0.0F)) {
            poseStack.mulPose(new Quaternionf().setAngleAxis(boat.getBubbleAngle(partialTicks) * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        this.model.setupAnim(boat, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer vc = buffer.getBuffer(this.model.renderType(this.texture));
        //[[[cog
        // compat_core.emit_render_to_buffer(cog, ver)
        //]]]
        //[[[end]]]
        if (!boat.isUnderWater() && this.model instanceof WaterPatchModel waterPatch) {
            VertexConsumer waterVc = buffer.getBuffer(RenderType.waterMask());
            waterPatch.waterPatch().render(poseStack, waterVc, packedLight, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
        super.render(boat, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(Boat boat) {
        return this.texture;
    }
}
