package dev.screret.modularui.drawable.text;

import dev.screret.modularui.api.drawable.IKey;
import dev.screret.modularui.client.screen.viewport.GuiContext;
import dev.screret.modularui.theme.WidgetTheme;
import dev.screret.modularui.utils.Alignment;
import dev.screret.modularui.widgets.TextWidget;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import lombok.Getter;

import java.util.function.IntSupplier;

import org.jetbrains.annotations.Nullable;

public class StyledText extends BaseKey {

    private final IKey key;
    @Getter
    private Alignment alignment = Alignment.Center;
    @Getter
    private IntSupplier color = null;
    private @Nullable Boolean shadow = null;
    @Getter
    private float scale = 1f;

    public StyledText(IKey key) {
        this.key = key;
    }

    @Override
    public MutableComponent get() {
        return this.key.get();
    }

    @Override
    public MutableComponent getFormatted() {
        return this.key.getFormatted();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        renderer.setAlignment(this.alignment, width, height);
        renderer.setColor(this.color != null ? this.color.getAsInt() : widgetTheme.getTextColor());
        renderer.setScale(this.scale);
        renderer.setPos(x, y);
        renderer.setShadow(this.shadow != null ? this.shadow : widgetTheme.isTextShadow());
        renderer.draw(context.getGraphics(), getFormatted());
    }

    public @Nullable Boolean isShadow() {
        return this.shadow;
    }

    @Override
    public BaseKey style(ChatFormatting formatting) {
        this.key.style(formatting);
        return this;
    }

    @Override
    public StyledText alignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    @Override
    public StyledText color(int color) {
        return color(() -> color);
    }

    @Override
    public StyledText color(@Nullable IntSupplier color) {
        this.color = color;
        return this;
    }

    @Override
    public StyledText scale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public StyledText shadow(@Nullable Boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    @Override
    public TextWidget<?> asWidget() {
        return new TextWidget<>(this.key)
                .alignment(this.alignment)
                .color(this.color)
                .scale(this.scale)
                .shadow(this.shadow);
    }

    @Override
    public AnimatedText withAnimation() {
        return new AnimatedText(this.key)
                .alignment(this.alignment)
                .color(this.color)
                .scale(this.scale)
                .shadow(this.shadow);
    }
}
