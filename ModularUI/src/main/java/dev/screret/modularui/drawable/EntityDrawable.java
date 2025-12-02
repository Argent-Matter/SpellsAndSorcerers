package dev.screret.modularui.drawable;

import dev.screret.modularui.api.drawable.IDrawable;
import dev.screret.modularui.client.screen.viewport.GuiContext;
import dev.screret.modularui.theme.WidgetTheme;

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
