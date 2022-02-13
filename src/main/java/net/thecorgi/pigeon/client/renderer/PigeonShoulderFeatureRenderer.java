package net.thecorgi.pigeon.client.renderer;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.entity.PigeonEntity;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class PigeonShoulderFeatureRenderer extends GeoLayerRenderer<PigeonEntity> {
    private final IGeoRenderer<PigeonEntity> renderer;
    boolean leftShoulder;

    public PigeonShoulderFeatureRenderer(IGeoRenderer<PigeonEntity> entityRendererIn) {
        super(entityRendererIn);
        this.renderer = entityRendererIn;
        this.leftShoulder = false;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int packedLightIn,
                       PigeonEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getOwner() != null) {
            PlayerEntity owner = (PlayerEntity) entity.getOwner();

            NbtCompound compoundTag = this.leftShoulder ? owner.getShoulderEntityLeft() : owner.getShoulderEntityRight();
            EntityType.get(compoundTag.getString("id")).filter((entityType) -> {
                return entityType == PigeonPost.PIGEON;
            }).ifPresent((entityType) -> {
                matrices.push();
                matrices.translate(leftShoulder ? 0.4000000059604645D : -0.4000000059604645D, owner.isInSneakingPose() ? -1.2999999523162842D : -1.5D, 0.0D);
                matrices.pop();
            });
        }
    }
}