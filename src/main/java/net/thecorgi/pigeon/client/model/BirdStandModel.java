package net.thecorgi.pigeon.client.model;

import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.util.Identifier;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.block.BirdStandBlock;
import net.thecorgi.pigeon.block.BirdStandBlockEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BirdStandModel extends AnimatedGeoModel<BirdStandBlockEntity> {
    @Override
    public Identifier getModelLocation(BirdStandBlockEntity object) {
        return PigeonPost.id("geo/bird_stand.geo.json");
    }

    @Override
    public Identifier getTextureLocation(BirdStandBlockEntity object) {
        return PigeonPost.id("textures/block/bird_stand.png");
    }

    @Override
    public Identifier getAnimationFileLocation(BirdStandBlockEntity object) {
        return PigeonPost.id("animations/bird_stand.animation.json");
    }
}