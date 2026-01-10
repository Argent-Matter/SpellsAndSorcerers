package dev.screret.modularui.value.sync;

import dev.screret.modularui.api.value.IStringValue;
import dev.screret.modularui.utils.ICopy;
import dev.screret.modularui.utils.serialization.network.ByteBufAdapters;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BigDecimalSyncValue extends GenericSyncValue<ByteBuf, BigDecimal> implements IStringValue<BigDecimal> {

    public BigDecimalSyncValue(@NotNull Supplier<BigDecimal> getter, @Nullable Consumer<BigDecimal> setter) {
        super(BigDecimal.class, getter, setter, ByteBufAdapters.BIG_DECIMAL, ICopy.immutable());
    }

    @Override
    public String getStringValue() {
        return getValue().toString();
    }

    @Override
    public void setStringValue(String val) {
        setValue(new BigDecimal(val));
    }
}
