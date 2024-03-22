package screret.sas.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import screret.sas.Util;
import screret.sas.attachmenttypes.ModAttachmentTypes;
import screret.sas.config.SASConfig;

public class ManaBarOverlay implements IGuiOverlay {

    public static final ResourceLocation MANA_BAR_LOCATION = Util.id("textures/gui/mana_bar.png");

    @Override
    public void render(ExtendedGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (SASConfig.Server.useMana.get()) {
            RenderSystem.setShaderTexture(0, MANA_BAR_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            if (gui.shouldDrawSurvivalElements() && gui.getMinecraft().player != null) {
                gui.getMinecraft().getProfiler().push("manaBar");

                if (gui.getMinecraft().player.hasData(ModAttachmentTypes.MANA)) {
                    var capability = gui.getMinecraft().player.getData(ModAttachmentTypes.MANA);
                    int left = screenWidth / 2 + SASConfig.Client.manaBarX.get();
                    int top = screenHeight - SASConfig.Client.manaBarY.get();

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
