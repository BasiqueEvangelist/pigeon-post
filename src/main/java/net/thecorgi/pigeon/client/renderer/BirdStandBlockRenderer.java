package net.thecorgi.pigeon.client.renderer;

import net.thecorgi.pigeon.block.BirdStandBlockEntity;
import net.thecorgi.pigeon.client.model.BirdStandModel;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class BirdStandBlockRenderer extends GeoBlockRenderer<BirdStandBlockEntity> {
    public BirdStandBlockRenderer() {
        super(new BirdStandModel());
    }
}