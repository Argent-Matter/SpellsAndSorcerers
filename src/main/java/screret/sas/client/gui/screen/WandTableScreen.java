package screret.sas.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import screret.sas.Util;
import screret.sas.container.container.WandTableMenu;

public class WandTableScreen extends AbstractContainerScreen<WandTableMenu> {
    private static final ResourceLocation WAND_TABLE_LOCATION = Util.id("textures/gui/container/wand_table.png");

    public WandTableScreen(WandTableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, WAND_TABLE_LOCATION);
        pGuiGraphics.blit(WAND_TABLE_LOCATION, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
