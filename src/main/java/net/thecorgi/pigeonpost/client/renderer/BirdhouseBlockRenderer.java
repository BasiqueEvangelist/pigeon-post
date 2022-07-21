package net.thecorgi.pigeonpost.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.thecorgi.pigeonpost.client.model.BirdhouseBlockModel;
import net.thecorgi.pigeonpost.common.block.BirdhouseBlockEntity;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

import java.util.Optional;

public class BirdhouseBlockRenderer extends GeoBlockRenderer<BirdhouseBlockEntity> {
    public BirdhouseBlockRenderer() {
        super(new BirdhouseBlockModel());
    }

    @Override
    public void render(GeoModel model, BirdhouseBlockEntity animatable, float partialTicks, RenderLayer type,
                       MatrixStack matrixStackIn, VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder,
                       int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

        Optional<GeoBone> bag = model.getBone("bag");
        bag.ifPresent(geoBone -> geoBone.setHidden(!animatable.hasStoredItems()));

        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
                packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
