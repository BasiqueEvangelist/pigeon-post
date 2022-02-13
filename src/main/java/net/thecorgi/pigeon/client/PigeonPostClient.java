package net.thecorgi.pigeon.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.thecorgi.pigeon.PigeonPost;
import net.thecorgi.pigeon.client.renderer.BirdStandBlockRenderer;
import net.thecorgi.pigeon.client.renderer.PigeonEntityRenderer;

public class PigeonPostClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(PigeonPost.BIRD_STAND_BLOCK_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new BirdStandBlockRenderer());
        EntityRendererRegistry.register(PigeonPost.PIGEON, PigeonEntityRenderer::new);
    }
}
