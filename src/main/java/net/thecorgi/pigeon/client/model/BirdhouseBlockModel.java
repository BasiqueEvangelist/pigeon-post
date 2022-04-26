package net.thecorgi.pigeon.client.model;

import net.minecraft.util.Identifier;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.block.BirdhouseBlockEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BirdhouseBlockModel extends AnimatedGeoModel<BirdhouseBlockEntity> {
    @Override
    public Identifier getModelLocation(BirdhouseBlockEntity object) {
        return PigeonPost.id("geo/birdhouse.geo.json");
    }

    @Override
    public Identifier getTextureLocation(BirdhouseBlockEntity object) {
        return PigeonPost.id("textures/block/birdhouse.png");
    }

    @Override
    public Identifier getAnimationFileLocation(BirdhouseBlockEntity object) {
        return PigeonPost.id("animations/birdhouse.animation.json");
    }
}