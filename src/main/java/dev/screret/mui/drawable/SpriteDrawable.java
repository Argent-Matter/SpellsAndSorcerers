package dev.screret.mui.drawable;

import dev.screret.mui.api.drawable.IDrawable;
import dev.screret.mui.client.screen.viewport.GuiContext;
import dev.screret.mui.theme.WidgetTheme;
import dev.screret.mui.widget.Widget;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteDrawable implements IDrawable {

    private final TextureAtlasSprite sprite;
    private boolean canApplyTheme = false;

    public SpriteDrawable(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        GuiDraw.drawSprite(context.getLastGraphicsPose(), this.sprite, x, y, width, height);
    }

    @Override
    public Widget<?> asWidget() {
        return IDrawable.super.asWidget().size(this.sprite.contents().width(), this.sprite.contents().height());
    }

    @Override
    public Icon asIcon() {
        return IDrawable.super.asIcon().size(this.sprite.contents().width(), this.sprite.contents().height());
    }

    public SpriteDrawable canApplyTheme(boolean canApplyTheme) {
        this.canApplyTheme = canApplyTheme;
        return this;
    }

    @Override
    public boolean canApplyTheme() {
        return canApplyTheme;
    }
}
