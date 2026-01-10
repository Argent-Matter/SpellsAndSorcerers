package dev.screret.modularui.drawable;

import dev.screret.modularui.api.IJsonSerializable;
import dev.screret.modularui.api.drawable.IDrawable;
import dev.screret.modularui.client.screen.viewport.GuiContext;
import dev.screret.modularui.theme.WidgetTheme;
import dev.screret.modularui.utils.Color;
import dev.screret.modularui.utils.serialization.json.JsonHelper;

import com.google.gson.JsonObject;
import lombok.Getter;

public class Scrollbar implements IDrawable, IJsonSerializable<Scrollbar> {

    public static final Scrollbar DEFAULT = new Scrollbar(false);
    public static final Scrollbar VANILLA = new Scrollbar(true);

    public static Scrollbar ofJson(JsonObject json) {
        if (JsonHelper.getBoolean(json, false, "striped", "vanilla")) {
            return VANILLA;
        }
        return DEFAULT;
    }

    @Getter
    private final boolean striped;

    public Scrollbar(boolean striped) {
        this.striped = striped;
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        GuiDraw.drawRect(context.getGraphics(), x, y, width, height, Color.mix(0xFFEEEEEE, widgetTheme.getColor()));
        GuiDraw.drawRect(context.getGraphics(), x + 1, y + 1, width - 1, height - 1,
                Color.mix(0xFF666666, widgetTheme.getColor()));
        GuiDraw.drawRect(context.getGraphics(), x + 1, y + 1, width - 2, height - 2,
                Color.mix(0xFFAAAAAA, widgetTheme.getColor()));

        if (isStriped()) {
            if (height <= 5 && width <= 5) return;
            int color = widgetTheme.getTextColor();
            if (height >= width) {
                int start = y + 2;
                int end = height + start - 4;
                for (int cy = start; cy < end; cy += 2) {
                    GuiDraw.drawRect(context.getGraphics(), x + 2, cy, width - 4, 1, color);
                }
            } else {
                int start = x + 2;
                int end = width + start - 4;
                for (int cx = start; cx < end; cx += 2) {
                    GuiDraw.drawRect(context.getGraphics(), cx, y + 2, 1, height - 4, color);
                }
            }
        }
    }

    @Override
    public boolean canApplyTheme() {
        return true;
    }

    @Override
    public boolean saveToJson(JsonObject json) {
        json.addProperty("striped", this.striped);
        return true;
    }
}
