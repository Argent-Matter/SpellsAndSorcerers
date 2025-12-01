package dev.screret.mui.widgets;

import dev.screret.mui.api.ITheme;
import dev.screret.mui.api.value.IValue;
import dev.screret.mui.drawable.GuiDraw;
import dev.screret.mui.theme.WidgetThemeEntry;
import dev.screret.mui.value.ObjectValue;
import dev.screret.mui.value.sync.SyncHandler;
import dev.screret.mui.widget.Widget;
import dev.screret.mui.client.screen.viewport.ModularGuiContext;

import net.minecraft.world.item.ItemStack;

public class ItemDisplayWidget extends Widget<ItemDisplayWidget> {

    private IValue<ItemStack> value;
    private boolean displayAmount = false;

    public ItemDisplayWidget() {
        size(18);
    }

    @Override
    public boolean isValidSyncHandler(SyncHandler syncHandler) {
        this.value = castIfTypeGenericElseNull(syncHandler, ItemStack.class);
        return this.value != null;
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
        this.value = itemSupplier;
        setValue(itemSupplier);
        return this;
    }

    public ItemDisplayWidget item(ItemStack itemStack) {
        ;
        return item(new ObjectValue<>(itemStack));
    }

    public ItemDisplayWidget displayAmount(boolean displayAmount) {
        this.displayAmount = displayAmount;
        return this;
    }
}
