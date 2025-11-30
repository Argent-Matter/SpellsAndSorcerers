package dev.screret.motm.client.gui.overlay;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.capability.mana.Mana;
import dev.screret.motm.config.MOTMConfig;
import dev.screret.motm.data.MOTMAttachmentTypes;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.systems.RenderSystem;

import org.jetbrains.annotations.NotNull;

public class ManaBarOverlay implements LayeredDraw.Layer {

    public static final ResourceLocation MANA_BAR_LOCATION = MOTMUtil.id("textures/gui/mana_bar.png");

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (!MOTMConfig.Server.useMana.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.gameMode.canHurtPlayer() || minecraft.player == null) {
            return;
        }

        RenderSystem.setShaderTexture(0, MANA_BAR_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();

        if (minecraft.player.hasData(MOTMAttachmentTypes.MANA)) {
            minecraft.getProfiler().push("manaBar");

            int left = minecraft.getWindow().getWidth() / 2 + MOTMConfig.Client.manaBarX.get();
            int top = minecraft.getWindow().getHeight() - MOTMConfig.Client.manaBarY.get();

            Mana mana = minecraft.player.getData(MOTMAttachmentTypes.MANA);
            int progress = (int) ((mana.getMana() / (float) mana.getMaxMana()) * 80);
            guiGraphics.blitSprite(MANA_BAR_LOCATION, left, top, 0, 80, 5);
            if (progress > 0) {
                guiGraphics.blitSprite(MANA_BAR_LOCATION, left, top, 0, progress, 5);
            }

            minecraft.getProfiler().pop();
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
