package dev.screret.modularui.integration.recipeviewer;

import dev.screret.modularui.api.IMuiScreen;
import dev.screret.modularui.client.screen.ModularScreen;
import dev.screret.modularui.utils.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import lombok.Getter;

public class RecipeViewerScreenWrapper implements IMuiScreen {

    @Getter
    private final ModularScreen screen;

    public RecipeViewerScreenWrapper(ModularScreen screen) {
        this.screen = screen;
    }

    @Override
    public Screen getWrappedScreen() {
        return Minecraft.getInstance().screen;
    }

    @Override
    public void updateGuiArea(Rectangle area) {
        // overlay should not modify screen
    }
}
