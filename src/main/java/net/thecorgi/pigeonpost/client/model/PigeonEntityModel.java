package net.thecorgi.pigeonpost.client.model;

import net.minecraft.util.Identifier;
import net.thecorgi.pigeonpost.PigeonPost;
import net.thecorgi.pigeonpost.common.entity.PigeonEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class PigeonEntityModel extends AnimatedGeoModel<PigeonEntity> {
    @Override
    public Identifier getModelLocation(PigeonEntity object) {
        return PigeonPost.id("geo/pigeonpost.geo.json");
    }

    @Override
    public Identifier getTextureLocation(PigeonEntity object) {
        return PigeonPost.id("textures/entity/pigeonpost/pigeon_" + object.getVariant() + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(PigeonEntity object) {
        return PigeonPost.id("animations/pigeonpost.animation.json");
    }

    @Override
    public void setLivingAnimations(PigeonEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if (!entity.hasVehicle()) {
            IBone head = this.getAnimationProcessor().getBone("head");

            EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
            if (head != null) {
                head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
                head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
            }
        }
    }
}