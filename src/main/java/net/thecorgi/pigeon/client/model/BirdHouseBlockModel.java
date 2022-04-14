package net.thecorgi.pigeon.client.model;

import net.minecraft.util.Identifier;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.common.block.BirdHouseBlockEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BirdHouseBlockModel extends AnimatedGeoModel<BirdHouseBlockEntity> {
    @Override
    public Identifier getModelLocation(BirdHouseBlockEntity object) {
        return PigeonPost.id("geo/bird_house.geo.json");
    }

    @Override
    public Identifier getTextureLocation(BirdHouseBlockEntity object) {
        return PigeonPost.id("textures/block/bird_house.png");
    }

    @Override
    public Identifier getAnimationFileLocation(BirdHouseBlockEntity object) {
        return PigeonPost.id("animations/bird_house.animation.json");
    }
}