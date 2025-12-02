package dev.screret.modularui.drawable;

import dev.screret.modularui.api.IJsonSerializable;
import dev.screret.modularui.api.drawable.IDrawable;
import dev.screret.modularui.client.screen.viewport.GuiContext;
import dev.screret.modularui.theme.WidgetTheme;
import dev.screret.modularui.widget.Widget;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import lombok.Getter;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class ItemDrawable implements IDrawable, IJsonSerializable<ItemDrawable> {

    public static final Codec<ItemDrawable> CODEC = ExtraCodecs.optionalEmptyMap(ItemStack.SINGLE_ITEM_CODEC)
            .xmap(stack -> stack.map(ItemDrawable::new).orElseGet(ItemDrawable::new),
                    stack -> stack.item.isEmpty() ? Optional.empty() : Optional.of(stack.item));

    @Getter
    private ItemStack item = ItemStack.EMPTY;

    public ItemDrawable() {}

    public ItemDrawable(@NotNull ItemStack item) {
        setItem(item);
    }

    public ItemDrawable(@NotNull Item item) {
        setItem(item);
    }

    public ItemDrawable(@NotNull Item item, int amount) {
        setItem(item, amount);
    }

    public ItemDrawable(@NotNull Item item, int amount, @NotNull DataComponentPatch componentPatch) {
        setItem(item, amount, componentPatch);
    }

    public ItemDrawable(@NotNull Block item) {
        setItem(item);
    }

    public ItemDrawable(@NotNull Block item, int amount) {
        setItem(new ItemStack(item, amount));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        applyColor(widgetTheme.getColor());
        GuiDraw.drawItem(context.getGraphics(), this.item, x, y, width, height, context.getCurrentDrawingZ());
    }

    @Override
    public int getDefaultWidth() {
        return 16;
    }

    @Override
    public int getDefaultHeight() {
        return 16;
    }

    @Override
    public Widget<?> asWidget() {
        return IDrawable.super.asWidget().size(16);
    }

    public ItemDrawable setItem(@NotNull ItemStack item) {
        this.item = item;
        return this;
    }

    public ItemDrawable setItem(@NotNull Item item) {
        return setItem(item, 1, DataComponentPatch.EMPTY);
    }

    public ItemDrawable setItem(@NotNull Item item, int amount) {
        return setItem(item, amount, DataComponentPatch.EMPTY);
    }

    public ItemDrawable setItem(@NotNull Item item, int amount, @NotNull DataComponentPatch componentPatch) {
        ItemStack stack = new ItemStack(item, amount);
        stack.applyComponents(componentPatch);
        return setItem(stack);
    }

    public ItemDrawable setItem(@NotNull Block item) {
        return setItem(item, 1);
    }

    public ItemDrawable setItem(@NotNull Block item, int amount) {
        return setItem(new ItemStack(item, amount));
    }

    @Override
    public Codec<ItemDrawable> getCodec() {
        return CODEC;
    }

    public static ItemDrawable ofJson(JsonObject json) {
        return CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
    }

    @Override
    public boolean saveToJson(JsonObject json) {
        if (this.item == null || this.item.isEmpty()) {
            return true;
        }
        JsonElement saved = CODEC.encodeStart(JsonOps.INSTANCE, this).getOrThrow(JsonParseException::new);
        if (saved.isJsonObject()) {
            saved.getAsJsonObject().asMap().forEach(json::add);
        }
        return true;
    }
}
