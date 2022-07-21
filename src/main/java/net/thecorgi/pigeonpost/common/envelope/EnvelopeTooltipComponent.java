package net.thecorgi.pigeonpost.common.envelope;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class EnvelopeTooltipComponent implements TooltipComponent {
    public static final Identifier TEXTURE = new Identifier("textures/gui/container/bundle.png");
    private final DefaultedList<ItemStack> inventory;
    private final int inventorySize;

    public EnvelopeTooltipComponent(EnvelopeTooltipData data) {
        this.inventory = data.getInventory();
        this.inventorySize = data.getInventorySize();
    }

    public int getHeight() {
        return this.getRows() * 20 + 2 + 4;
    }

    public int getWidth(TextRenderer textRenderer) {
        return this.getColumns() * 18 + 2;
    }

    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        int i = this.getColumns();
        int j = this.getRows();
        boolean bl = this.inventorySize >= 64;
        int k = 0;

        for(int l = 0; l < j; ++l) {
            for(int m = 0; m < i; ++m) {
                int n = x + m * 18 + 1;
                int o = y + l * 20 + 1;
                this.drawSlot(n, o, k++, bl, textRenderer, matrices, itemRenderer, z);
            }
        }

        this.drawOutline(x, y, i, j, matrices, z);
    }

    private void drawSlot(int x, int y, int index, boolean shouldBlock, TextRenderer textRenderer, MatrixStack matrices, ItemRenderer itemRenderer, int z) {
        if (index >= this.inventory.size()) {
            this.draw(matrices, x, y, z, shouldBlock ? EnvelopeTooltipComponent.Sprite.BLOCKED_SLOT : EnvelopeTooltipComponent.Sprite.SLOT);
        } else {
            ItemStack itemStack = this.inventory.get(index);
            this.draw(matrices, x, y, z, EnvelopeTooltipComponent.Sprite.SLOT);
            itemRenderer.renderInGuiWithOverrides(itemStack, x + 1, y + 1, index);
            itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x + 1, y + 1);
        }
    }

    private void drawOutline(int x, int y, int columns, int rows, MatrixStack matrices, int z) {
        this.draw(matrices, x, y, z, EnvelopeTooltipComponent.Sprite.BORDER_CORNER_TOP);
        this.draw(matrices, x + columns * 18 + 1, y, z, EnvelopeTooltipComponent.Sprite.BORDER_CORNER_TOP);

        int i;
        for(i = 0; i < columns; ++i) {
            this.draw(matrices, x + 1 + i * 18, y, z, EnvelopeTooltipComponent.Sprite.BORDER_HORIZONTAL_TOP);
            this.draw(matrices, x + 1 + i * 18, y + rows * 20, z, EnvelopeTooltipComponent.Sprite.BORDER_HORIZONTAL_BOTTOM);
        }

        for(i = 0; i < rows; ++i) {
            this.draw(matrices, x, y + i * 20 + 1, z, EnvelopeTooltipComponent.Sprite.BORDER_VERTICAL);
            this.draw(matrices, x + columns * 18 + 1, y + i * 20 + 1, z, EnvelopeTooltipComponent.Sprite.BORDER_VERTICAL);
        }

        this.draw(matrices, x, y + rows * 20, z, EnvelopeTooltipComponent.Sprite.BORDER_CORNER_BOTTOM);
        this.draw(matrices, x + columns * 18 + 1, y + rows * 20, z, EnvelopeTooltipComponent.Sprite.BORDER_CORNER_BOTTOM);
    }

    private void draw(MatrixStack matrices, int x, int y, int z, EnvelopeTooltipComponent.Sprite sprite) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        DrawableHelper.drawTexture(matrices, x, y, z, (float)sprite.u, (float)sprite.v, sprite.width, sprite.height, 128, 128);
    }

    private int getColumns() {
        return Math.max(3, (int)Math.ceil(Math.sqrt(this.inventory.size() + 1.0D)));
    }

    private int getRows() {
        return (int)Math.ceil((double)this.inventory.size() / this.getColumns());
    }

    @Environment(EnvType.CLIENT)
    private enum Sprite {
        SLOT(0, 0, 18, 20),
        BLOCKED_SLOT(0, 40, 18, 20),
        BORDER_VERTICAL(0, 18, 1, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
        BORDER_CORNER_TOP(0, 20, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1);

        public final int u;
        public final int v;
        public final int width;
        public final int height;

        Sprite(int u, int v, int width, int height) {
            this.u = u;
            this.v = v;
            this.width = width;
            this.height = height;
        }
    }

}
