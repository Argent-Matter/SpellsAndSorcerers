package screret.sas.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import screret.sas.Util;
import screret.sas.api.capability.mana.ManaProvider;
import screret.sas.config.SASConfig;

public class ManaBarOverlay implements IGuiOverlay {

    public static final ResourceLocation MANA_BAR_LOCATION = Util.id("textures/gui/mana_bar.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if(SASConfig.Server.useMana.get()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            if (gui.shouldDrawSurvivalElements() && gui.getMinecraft().player != null) {
                gui.getMinecraft().getProfiler().push("manaBar");

                var cap = gui.getMinecraft().player.getCapability(ManaProvider.MANA).resolve();
                if(cap.isPresent()) {
                    var capability = cap.get();
                    int left = screenWidth / 2 + SASConfig.Client.manaBarX.get();
                    int top = screenHeight - SASConfig.Client.manaBarY.get();

                    int progress = (int) ((capability.getManaStored() / (float)capability.getMaxManaStored()) * 80);
                    guiGraphics.blit(MANA_BAR_LOCATION, left, top, 0, 0, 80, 5);
                    if (progress > 0) {
                        guiGraphics.blit(MANA_BAR_LOCATION, left, top, 0, 5, progress, 5);
                    }

                    gui.getMinecraft().getProfiler().pop();
                }
            }
            RenderSystem.enableBlend();
        }
    }
}
