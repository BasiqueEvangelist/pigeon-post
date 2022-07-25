package net.thecorgi.pigeonpost.client.model;

import net.minecraft.util.Identifier;
import net.thecorgi.pigeonpost.PigeonPost;
import net.thecorgi.pigeonpost.common.block.BirdhouseBlockEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BirdhouseBlockModel extends AnimatedGeoModel<BirdhouseBlockEntity> {
    @Override
    public Identifier getModelResource(BirdhouseBlockEntity object) {
        return PigeonPost.id("geo/birdhouse.geo.json");
    }

    @Override
    public Identifier getTextureResource(BirdhouseBlockEntity object) {
        if (object.hasPigeon()) {
            return PigeonPost.id("textures/block/birdhouse_full.png");
        } else {
            return PigeonPost.id("textures/block/birdhouse_empty.png");
        }
    }

    @Override
    public Identifier getAnimationResource(BirdhouseBlockEntity object) {
        return PigeonPost.id("animations/birdhouse.animation.json");
    }
}