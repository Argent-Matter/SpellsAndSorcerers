package dev.screret.mui.value.sync;

import dev.screret.mui.utils.serialization.network.ByteBufAdapters;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LongArraySyncValue extends GenericSyncValue<ByteBuf, long[]> {

    public LongArraySyncValue(@NotNull Supplier<long[]> getter, @Nullable Consumer<long[]> setter) {
        super(getter, setter, ByteBufAdapters.LONG_ARR, long[]::clone);
    }
}
