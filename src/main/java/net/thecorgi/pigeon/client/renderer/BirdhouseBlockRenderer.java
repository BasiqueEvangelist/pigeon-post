package net.thecorgi.pigeon.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.thecorgi.pigeon.client.model.BirdhouseBlockModel;
import net.thecorgi.pigeon.common.block.BirdhouseBlockEntity;
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
        System.out.println(bag.isPresent());
        if (bag.isPresent()) {
            if (animatable.hasPigeon()) {
                model.getBone("bag").get().setHidden(false);
                System.out.println("BAGBAGBAGBAG");
            } else {
                model.getBone("bag").get().setHidden(true);
                System.out.println("NO BAG DETECTED");
            }
        }

        super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
                packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
