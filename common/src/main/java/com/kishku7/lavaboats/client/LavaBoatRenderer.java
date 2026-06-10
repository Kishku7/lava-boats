package com.kishku7.lavaboats.client;

import com.kishku7.lavaboats.LavaBoats;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
 * Renders a lava boat with our custom texture and VANILLA boat geometry. Vanilla's private
 * {@code BoatRenderer#getModelWithLocation} can't be intercepted cross-loader (unmapped on Forge),
 * so we extend {@link EntityRenderer} directly and reproduce vanilla {@code BoatRenderer.render}
 * with our own fixed texture + model (one renderer instance per boat type). Plain boats use
 * {@link BoatModel}, chest boats use {@link ChestBoatModel}, matching vanilla.
 */
@Environment(EnvType.CLIENT)
public class LavaBoatRenderer extends EntityRenderer<Boat> {

    public static final ResourceLocation CRIMSON =
            new ResourceLocation(LavaBoats.MOD_ID, "textures/entity/boat/crimson.png");
    public static final ResourceLocation WARPED =
            new ResourceLocation(LavaBoats.MOD_ID, "textures/entity/boat/warped.png");
    public static final ResourceLocation CRIMSON_CHEST =
            new ResourceLocation(LavaBoats.MOD_ID, "textures/entity/chest_boat/crimson.png");
    public static final ResourceLocation WARPED_CHEST =
            new ResourceLocation(LavaBoats.MOD_ID, "textures/entity/chest_boat/warped.png");

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
        this.model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
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
