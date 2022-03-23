package net.thecorgi.pigeon.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.thecorgi.pigeon.common.handler.EnvelopeScreenHandler;

import static net.thecorgi.pigeon.PigeonPost.id;

@Environment(EnvType.CLIENT)
public class EnvelopeScreen extends HandledScreen<EnvelopeScreenHandler> {
    private static final Identifier TEXTURE = id("textures/gui/container/envelope.png");
    private final int rows;

    public EnvelopeScreen(EnvelopeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
//        this.passEvents = false;
        this.rows = 6;
        this.backgroundHeight = 133;
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    protected void drawBackground(MatrixStack matrixStack, float f, int mouseY, int i) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader); // experimental
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int j = (this.width - this.backgroundWidth) / 2;
        int k = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrixStack, j, k, 0, 0, this.backgroundWidth, this.rows * 18 + 17);
        this.drawTexture(matrixStack, j, k + this.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }

    protected void drawForeground(MatrixStack matrixStack, int i, int j) {
        this.textRenderer.draw(matrixStack, this.title, 66.5F, 8.5F, 4210752);
        this.textRenderer.draw(matrixStack, this.playerInventoryTitle, 8.0F, (float)(this.backgroundHeight - 96 + 2), 4210752);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}