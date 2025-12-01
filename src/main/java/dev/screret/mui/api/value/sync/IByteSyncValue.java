package dev.screret.mui.api.value.sync;

import dev.screret.mui.api.value.IByteValue;

public interface IByteSyncValue<T> extends IByteValue<T>, IValueSyncHandler<T> {

    @Override
    default void setByteValue(byte val) {
        setByteValue(val, true);
    }

    default void setByteValue(byte val, boolean setSource) {
        setByteValue(val, setSource, true);
    }

    void setByteValue(byte value, boolean setSource, boolean sync);
}
