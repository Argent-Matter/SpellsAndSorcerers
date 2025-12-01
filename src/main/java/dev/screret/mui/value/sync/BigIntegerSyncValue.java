package dev.screret.mui.value.sync;

import dev.screret.mui.api.value.IStringValue;
import dev.screret.mui.utils.ICopy;
import dev.screret.mui.utils.serialization.network.ByteBufAdapters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BigIntegerSyncValue extends GenericSyncValue<BigInteger> implements IStringValue<BigInteger> {

    public BigIntegerSyncValue(@NotNull Supplier<BigInteger> getter, @Nullable Consumer<BigInteger> setter) {
        super(getter, setter, ByteBufAdapters.BIG_INT, ICopy.immutable());
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
