package net.thecorgi.pigeon.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.thecorgi.pigeon.client.model.BirdHouseBlockModel;
import net.thecorgi.pigeon.common.block.BirdHouseBlockEntity;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class BirdHouseBlockRenderer extends GeoBlockRenderer<BirdHouseBlockEntity> {
    public BirdHouseBlockRenderer() {
        super(new BirdHouseBlockModel());
    }

    @Override
    public void render(GeoModel model, BirdHouseBlockEntity animatable, float partialTicks, RenderLayer type,
                       MatrixStack matrixStackIn, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                       int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (animatable.hasPigeon()) {
            model.getBone("bag").get().setHidden(false);
        } else {
            model.getBone("bag").get().setHidden(true);
        }

        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
                packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
