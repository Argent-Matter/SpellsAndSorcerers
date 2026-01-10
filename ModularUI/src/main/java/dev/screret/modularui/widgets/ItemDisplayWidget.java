package dev.screret.modularui.widgets;

import dev.screret.modularui.api.ITheme;
import dev.screret.modularui.api.value.ISyncOrValue;
import dev.screret.modularui.api.value.IValue;
import dev.screret.modularui.client.screen.viewport.ModularGuiContext;
import dev.screret.modularui.drawable.GuiDraw;
import dev.screret.modularui.integration.recipeviewer.entry.EntryList;
import dev.screret.modularui.integration.recipeviewer.entry.item.ItemStackList;
import dev.screret.modularui.integration.recipeviewer.handlers.IngredientProvider;
import dev.screret.modularui.theme.WidgetThemeEntry;
import dev.screret.modularui.value.ObjectValue;
import dev.screret.modularui.value.sync.SyncHandler;
import dev.screret.modularui.widget.Widget;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

public class ItemDisplayWidget extends Widget<ItemDisplayWidget> implements IngredientProvider<ItemStack> {

    private IValue<ItemStack> value;
    private boolean displayAmount = false;

    public ItemDisplayWidget() {
        size(18);
    }

    @Override
    public boolean isValidSyncOrValue(@NotNull ISyncOrValue syncOrValue) {
        return syncOrValue.isValueOfType(ItemStack.class);
    }

    @Override
    protected void setSyncOrValue(@NotNull ISyncOrValue syncOrValue) {
        super.setSyncOrValue(syncOrValue);
        this.value = syncOrValue.castValueNullable(ItemStack.class);
    }

    @Override
    protected WidgetThemeEntry<?> getWidgetThemeInternal(ITheme theme) {
        return theme.getItemSlotTheme();
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        ItemStack item = value.getValue();
        if (!item.isEmpty()) {
            GuiDraw.drawItem(context.getGraphics(), item, 1, 1, 16, 16, context.getCurrentDrawingZ());
            if (this.displayAmount) {
                GuiDraw.drawStandardSlotAmountText(context, item.getCount(), null, getArea(), 0);
            }
        }
    }

    public ItemDisplayWidget item(IValue<ItemStack> itemSupplier) {
        setSyncOrValue(ISyncOrValue.orEmpty(itemSupplier));
        return this;
    }

    public ItemDisplayWidget item(ItemStack itemStack) {
        return item(new ObjectValue<>(ItemStack.class, itemStack));
    }

    public ItemDisplayWidget displayAmount(boolean displayAmount) {
        this.displayAmount = displayAmount;
        return this;
    }

    @Override
    public EntryList<ItemStack> getIngredients() {
        return ItemStackList.of(value.getValue());
    }

    @Override
    public @NotNull Class<ItemStack> ingredientClass() {
        return ItemStack.class;
    }
}
