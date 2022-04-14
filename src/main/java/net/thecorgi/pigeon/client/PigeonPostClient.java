package net.thecorgi.pigeon.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.thecorgi.pigeon.client.renderer.BirdHouseBlockRenderer;
import net.thecorgi.pigeon.client.renderer.PigeonEntityRenderer;
import net.thecorgi.pigeon.client.screen.EnvelopeScreen;
import net.thecorgi.pigeon.common.handler.EnvelopeScreenHandler;
import net.thecorgi.pigeon.common.registry.BlockRegistry;
import net.thecorgi.pigeon.common.registry.EntityRegistry;

import static net.thecorgi.pigeon.PigeonPost.id;

@Environment(EnvType.CLIENT)
public class PigeonPostClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityRegistry.PIGEON, PigeonEntityRenderer::new);
        BlockEntityRendererRegistry.register(BlockRegistry.BIRD_HOUSE_BLOCK_ENTITY, (BlockEntityRendererFactory.Context rendererDispatcherIn) -> new BirdHouseBlockRenderer());

        String translationKey = Util.createTranslationKey("container", id("envelope"));

        ScreenProviderRegistry.INSTANCE.<EnvelopeScreenHandler>registerFactory(
                id("envelope"),
                (container -> new EnvelopeScreen(
                        container, MinecraftClient.getInstance().player.getInventory(),
                        new TranslatableText(translationKey)))
                );


//        ScreenRegistry.register(PigeonPost.ENVELOPE_SCREEN_HANDLER, EnvelopeScreen::new);
    }
}
