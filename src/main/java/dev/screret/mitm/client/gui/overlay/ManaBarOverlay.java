package dev.screret.mitm.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.capability.mana.Mana;
import dev.screret.mitm.data.MITMAttachmentTypes;
import dev.screret.mitm.config.MITMConfig;
import org.jetbrains.annotations.NotNull;

public class ManaBarOverlay implements LayeredDraw.Layer {

    public static final ResourceLocation MANA_BAR_LOCATION = MITMUtil.id("textures/gui/mana_bar.png");

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (!MITMConfig.Server.useMana.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.gameMode.canHurtPlayer() || minecraft.player == null) {
            return;
        }

        RenderSystem.setShaderTexture(0, MANA_BAR_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();


        if (minecraft.player.hasData(MITMAttachmentTypes.MANA)) {
            minecraft.getProfiler().push("manaBar");


            int left = minecraft.getWindow().getWidth() / 2 + MITMConfig.Client.manaBarX.get();
            int top = minecraft.getWindow().getHeight() - MITMConfig.Client.manaBarY.get();

            Mana mana = minecraft.player.getData(MITMAttachmentTypes.MANA);
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
