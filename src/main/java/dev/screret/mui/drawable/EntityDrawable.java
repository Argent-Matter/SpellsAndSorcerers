package dev.screret.mui.drawable;

import dev.screret.mui.api.drawable.IDrawable;
import dev.screret.mui.theme.WidgetTheme;
import dev.screret.mui.client.screen.viewport.GuiContext;

import net.minecraft.world.entity.LivingEntity;

public class EntityDrawable implements IDrawable {

    private LivingEntity entity;

    public EntityDrawable(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        GuiDraw.drawLivingEntity(context.getGraphics(), this.entity, x, y, width, height, context.getCurrentDrawingZ());
    }
}
