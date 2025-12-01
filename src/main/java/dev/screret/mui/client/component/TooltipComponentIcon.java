package dev.screret.mui.client.component;

import dev.screret.mui.api.drawable.IDrawable;
import dev.screret.mui.api.drawable.IIcon;
import dev.screret.mui.theme.WidgetTheme;
import dev.screret.mui.widget.sizer.Box;
import dev.screret.mui.client.screen.viewport.GuiContext;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

public class TooltipComponentIcon implements IIcon {

    @Getter
    private final ClientTooltipComponent clientComponent;

    public TooltipComponentIcon(TooltipComponent component) {
        this.clientComponent = ClientTooltipComponent.create(component);
    }

    @OnlyIn(Dist.CLIENT)
    public TooltipComponentIcon(ClientTooltipComponent clientComponent) {
        this.clientComponent = clientComponent;
    }

    @Override
    public @Nullable IDrawable getWrappedDrawable() {
        return null;
    }

    @Override
    public int getWidth() {
        return clientComponent.getWidth(Minecraft.getInstance().font);
    }

    @Override
    public int getHeight() {
        return clientComponent.getHeight();
    }

    @Override
    public Box getMargin() {
        return Box.ZERO;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        Font font = context.getFont();
        context.graphicsPose().pushPose();

        // rescale the tooltip component if we happen to go out of its bounds
        float ratio = 1.0f;
        if (width < clientComponent.getWidth(font)) {
            ratio = (float) width / clientComponent.getWidth(font);
        }
        if (height < clientComponent.getHeight()) {
            ratio = Math.min(ratio, (float) height / clientComponent.getHeight());
        }
        if (ratio != 1.0f) {
            context.graphicsPose().scale(ratio, ratio, 1.0f);
        }
        clientComponent.renderImage(font, x, y, context.getGraphics());

        context.graphicsPose().popPose();
    }
}
