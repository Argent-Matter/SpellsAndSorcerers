package dev.screret.modularui.widgets;

import dev.screret.modularui.api.value.ISyncOrValue;
import dev.screret.modularui.api.value.IValue;
import dev.screret.modularui.client.screen.RichTooltip;

import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidDisplayWidget extends AbstractFluidDisplayWidget<FluidDisplayWidget> {

    private IValue<FluidStack> value;
    private boolean displayAmount = true;

    @Override
    public boolean isValidSyncOrValue(@NotNull ISyncOrValue syncOrValue) {
        return syncOrValue.isValueOfType(FluidStack.class);
    }

    @Override
    protected void setSyncOrValue(@NotNull ISyncOrValue syncOrValue) {
        super.setSyncOrValue(syncOrValue);
        this.value = syncOrValue.castValueNullable(FluidStack.class);
    }

    @Override
    protected boolean displayAmountText() {
        return this.displayAmount;
    }

    @Override
    protected @Nullable FluidStack getFluidStack() {
        return this.value != null ? this.value.getValue() : null;
    }

    public FluidDisplayWidget displayAmount(boolean displayAmount) {
        this.displayAmount = displayAmount;
        return this;
    }

    public FluidDisplayWidget fluidTooltip(BiConsumer<RichTooltip, FluidStack> tooltip) {
        return tooltipAutoUpdate(true).tooltipBuilder(t -> tooltip.accept(t, getFluidStack()));
    }
}
