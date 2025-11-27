package dev.screret.mitm.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.data.MITMAttachmentTypes;
import dev.screret.mitm.config.MITMConfig;

public class ManaBarOverlay implements IGuiOverlay {

    public static final ResourceLocation MANA_BAR_LOCATION = MITMUtil.id("textures/gui/mana_bar.png");

    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (MITMConfig.Server.useMana.get()) {
            RenderSystem.setShaderTexture(0, MANA_BAR_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            if (gui.shouldDrawSurvivalElements() && gui.getMinecraft().player != null) {
                gui.getMinecraft().getProfiler().push("manaBar");

                if (gui.getMinecraft().player.hasData(MITMAttachmentTypes.MANA)) {
                    var capability = gui.getMinecraft().player.getData(MITMAttachmentTypes.MANA);
                    int left = screenWidth / 2 + MITMConfig.Client.manaBarX.get();
                    int top = screenHeight - MITMConfig.Client.manaBarY.get();

                    int progress = (int) ((capability.getManaStored() / (float) capability.getMaxManaStored()) * 80);
                    guiGraphics.blitSprite(MANA_BAR_LOCATION, left, top, 0, 80, 5);
                    if (progress > 0) {
                        guiGraphics.blitSprite(MANA_BAR_LOCATION, left, top, 0, progress, 5);
                    }

                    gui.getMinecraft().getProfiler().pop();
                }

            }
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

    }
}
