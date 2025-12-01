package dev.screret.mui.drawable.text;

import dev.screret.mui.api.drawable.IIcon;
import dev.screret.mui.client.screen.viewport.GuiContext;
import dev.screret.mui.theme.WidgetTheme;
import dev.screret.mui.utils.Alignment;
import dev.screret.mui.widget.sizer.Box;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import lombok.Getter;

import org.jetbrains.annotations.Nullable;

public class TextIcon implements IIcon {

    @Getter
    private final Component text;
    @Getter
    private final int width, height;
    private final float scale;
    private final Alignment alignment;
    private static final Box margin = new Box();

    public TextIcon(Component text, int width, int height, float scale, Alignment alignment) {
        this.text = text;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.alignment = alignment;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        TextRenderer.SHARED.setPos(x, y);
        TextRenderer.SHARED.setAlignment(this.alignment, width);
        TextRenderer.SHARED.setScale(this.scale);
        TextRenderer.SHARED.drawSimple(context.getGraphics(), this.text);
    }

    @Override
    @Nullable
    public IIcon getWrappedDrawable() {
        return null;
    }

    @Override
    public Box getMargin() {
        return margin;
    }
}
