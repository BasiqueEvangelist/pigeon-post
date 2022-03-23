package net.thecorgi.pigeon.client.model;

import net.minecraft.util.Identifier;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.entity.NestEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class NestModel extends AnimatedGeoModel<NestEntity> {
    @Override
    public Identifier getModelLocation(NestEntity object) {
        return PigeonPost.id("geo/nest.geo.json");
    }

    @Override
    public Identifier getTextureLocation(NestEntity object) {
        return PigeonPost.id("textures/block/nest.png");
    }

    @Override
    public Identifier getAnimationFileLocation(NestEntity object) {
        return PigeonPost.id("animations/nest.animation.json");
    }
}