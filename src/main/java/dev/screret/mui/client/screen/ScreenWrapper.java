package dev.screret.mui.client.screen;

import dev.screret.mui.api.IMuiScreen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ScreenWrapper extends Screen implements IMuiScreen {

    @Getter
    private final @NotNull ModularScreen screen;

    public ScreenWrapper(@NotNull ModularScreen screen) {
        super(Component.empty());
        this.screen = screen;
        this.screen.construct(this);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        handleDrawBackground(guiGraphics, mouseX, mouseY, partialTick, super::renderBackground);
    }

    @Override
    public boolean isPauseScreen() {
        return this.screen.isPauseScreen();
    }

    @Override
    public String toString() {
        return "Wrapper(" + getScreen() + ")";
    }
}
