package dev.screret.modularui.value.sync;

import dev.screret.modularui.api.value.IStringValue;
import dev.screret.modularui.utils.ICopy;
import dev.screret.modularui.utils.serialization.network.ByteBufAdapters;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BigIntegerSyncValue extends GenericSyncValue<ByteBuf, BigInteger> implements IStringValue<BigInteger> {

    public BigIntegerSyncValue(@NotNull Supplier<BigInteger> getter, @Nullable Consumer<BigInteger> setter) {
        super(BigInteger.class, getter, setter, ByteBufAdapters.BIG_INT, ICopy.immutable());
    }

    @Override
    public String getStringValue() {
        return getValue().toString();
    }

    @Override
    public void setStringValue(String val) {
        setValue(new BigInteger(val));
    }
}
