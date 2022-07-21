package net.thecorgi.pigeonpost.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.thecorgi.pigeonpost.PigeonPost;
import net.thecorgi.pigeonpost.client.renderer.BirdhouseBlockRenderer;
import net.thecorgi.pigeonpost.client.renderer.PigeonEntityRenderer;
import net.thecorgi.pigeonpost.common.envelope.EnvelopeGuiDescription;
import net.thecorgi.pigeonpost.common.envelope.EnvelopeScreen;
import net.thecorgi.pigeonpost.common.envelope.EnvelopeTooltipComponent;
import net.thecorgi.pigeonpost.common.envelope.EnvelopeTooltipData;
import net.thecorgi.pigeonpost.common.registry.BlockRegistry;
import net.thecorgi.pigeonpost.common.registry.EntityRegistry;

@Environment(EnvType.CLIENT)
public class PigeonPostClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityRegistry.PIGEON, PigeonEntityRenderer::new);

        TooltipComponentCallback.EVENT.register(data ->
        {
            if (data instanceof EnvelopeTooltipData) {
                return new EnvelopeTooltipComponent((EnvelopeTooltipData)data);
            }
            return null;
        });

        ScreenRegistry.<EnvelopeGuiDescription, EnvelopeScreen>register(PigeonPost.SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new EnvelopeScreen(gui, inventory.player, title));
        BlockEntityRendererRegistry.register(BlockRegistry.BIRDHOUSE_BLOCK_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new BirdhouseBlockRenderer());
    }
}
