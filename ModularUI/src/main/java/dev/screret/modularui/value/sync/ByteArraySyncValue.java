package dev.screret.modularui.value.sync;

import dev.screret.modularui.utils.serialization.network.ByteBufAdapters;

import io.netty.buffer.ByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ByteArraySyncValue extends GenericSyncValue<ByteBuf, byte[]> {

    public ByteArraySyncValue(@NotNull Supplier<byte[]> getter, @Nullable Consumer<byte[]> setter) {
        super(getter, setter, ByteBufAdapters.BYTE_ARR, byte[]::clone);
    }
}
