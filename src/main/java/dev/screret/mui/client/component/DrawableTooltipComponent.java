package dev.screret.mui.client.component;

import dev.screret.mui.api.drawable.IDrawable;
import dev.screret.mui.api.drawable.IIcon;
import dev.screret.mui.api.drawable.IKey;
import dev.screret.mui.theme.WidgetTheme;
import dev.screret.mui.client.screen.viewport.GuiContext;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class DrawableTooltipComponent implements ClientTooltipComponent, TooltipComponent {

    @Getter
    private final IDrawable drawable;

    public DrawableTooltipComponent(IDrawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public int getHeight() {
        if (drawable instanceof IIcon icon) {
            return icon.getHeight();
        } else if (drawable instanceof IKey key) {
            return key.asTextIcon().getHeight();
        } else {
            return 18;
        }
    }

    @Override
    public int getWidth(@NotNull Font font) {
        if (drawable instanceof IIcon icon) {
            return icon.getWidth();
        } else if (drawable instanceof IKey key) {
            return key.asTextIcon().getWidth();
        } else {
            return 18;
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        GuiContext context = GuiContext.getDefault();
        GuiGraphics lastGraphics = context.getGraphics();

        context.setGraphics(guiGraphics);
        drawable.draw(context, x, y, getWidth(font), getHeight(), WidgetTheme.getDefault().getTheme());
        context.setGraphics(lastGraphics);
    }
}
