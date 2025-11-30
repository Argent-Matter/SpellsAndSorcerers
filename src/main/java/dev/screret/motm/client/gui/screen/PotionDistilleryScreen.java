package dev.screret.motm.client.gui.screen;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.menu.container.PotionDistilleryMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import com.mojang.blaze3d.systems.RenderSystem;

public class PotionDistilleryScreen extends AbstractContainerScreen<PotionDistilleryMenu> {

    private static final ResourceLocation TEXTURE_LOCATION = MOTMUtil.id("textures/gui/container/potion_distillery.png");

    public PotionDistilleryScreen(PotionDistilleryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    public void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);

        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.isLit()) {
            int fuelAmount = this.menu.getLitProgress();
            guiGraphics.blit(TEXTURE_LOCATION, this.leftPos + 56 + PotionDistilleryMenu.FUEL_PROGRESS_BAR_X_SIZE - 1 - fuelAmount,
                    this.topPos + 44, PotionDistilleryMenu.FUEL_PROGRESS_BAR_X_SIZE - 1 - fuelAmount, 39, fuelAmount + 1, 4);
        }

        int progress = this.menu.getBurnProgress();
        guiGraphics.blit(TEXTURE_LOCATION, this.leftPos + 97, this.topPos + 16, 176, 0, 9, progress + 1);
    }
}
