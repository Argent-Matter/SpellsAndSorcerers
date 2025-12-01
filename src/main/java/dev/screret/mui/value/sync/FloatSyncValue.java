package dev.screret.mui.value.sync;

import dev.screret.mui.ModularUI;
import dev.screret.mui.api.value.sync.IDoubleSyncValue;
import dev.screret.mui.api.value.sync.IFloatSyncValue;
import dev.screret.mui.api.value.sync.IStringSyncValue;
import dev.screret.mui.utils.FloatConsumer;
import dev.screret.mui.utils.FloatSupplier;

import io.netty.buffer.ByteBuf;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FloatSyncValue extends ValueSyncHandler<ByteBuf, Float> implements
                            IFloatSyncValue<ByteBuf, Float>, IDoubleSyncValue<ByteBuf, Float>, IStringSyncValue<ByteBuf, Float> {

    private final FloatSupplier getter;
    private final FloatConsumer setter;
    private float cache;

    public FloatSyncValue(@NotNull FloatSupplier getter, @Nullable FloatConsumer setter) {
        this.getter = Objects.requireNonNull(getter);
        this.setter = setter;
        this.cache = getter.getAsFloat();
    }

    public FloatSyncValue(@NotNull FloatSupplier getter) {
        this(getter, (FloatConsumer) null);
    }

    @Contract("null, null -> fail")
    public FloatSyncValue(@Nullable FloatSupplier clientGetter,
                          @Nullable FloatSupplier serverGetter) {
        this(clientGetter, null, serverGetter, null);
    }

    @Contract("null, _, null, _ -> fail")
    public FloatSyncValue(@Nullable FloatSupplier clientGetter, @Nullable FloatConsumer clientSetter,
                          @Nullable FloatSupplier serverGetter, @Nullable FloatConsumer serverSetter) {
        if (clientGetter == null && serverGetter == null) {
            throw new NullPointerException("Client or server getter must not be null!");
        }
        if (ModularUI.isClientThread()) {
            this.getter = clientGetter != null ? clientGetter : serverGetter;
            this.setter = clientSetter != null ? clientSetter : serverSetter;
        } else {
            this.getter = serverGetter != null ? serverGetter : clientGetter;
            this.setter = serverSetter != null ? serverSetter : clientSetter;
        }
        this.cache = this.getter.getAsFloat();
    }

    @Override
    public Float getValue() {
        return this.cache;
    }

    @Override
    public void setValue(@NotNull Float value, boolean setSource, boolean sync) {
        setFloatValue(value, setSource, sync);
    }

    @Override
    public float getFloatValue() {
        return this.cache;
    }

    @Override
    public void setFloatValue(float value, boolean setSource, boolean sync) {
        this.cache = value;
        if (setSource && this.setter != null) {
            this.setter.accept(value);
        }
        if (sync) {
            sync(0, this::write);
        }
    }

    @Override
    public boolean updateCacheFromSource(boolean isFirstSync) {
        if (isFirstSync || this.getter.getAsFloat() != this.cache) {
            setFloatValue(this.getter.getAsFloat(), false, false);
            return true;
        }
        return false;
    }

    @Override
    public void notifyUpdate() {
        setFloatValue(this.getter.getAsFloat(), false, true);
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeFloat(getFloatValue());
    }

    @Override
    public void read(ByteBuf buffer) {
        setFloatValue(buffer.readFloat(), true, false);
    }

    @Override
    public void setStringValue(String value, boolean setSource, boolean sync) {
        setFloatValue(Float.parseFloat(value), setSource, sync);
    }

    @Override
    public String getStringValue() {
        return String.valueOf(this.cache);
    }

    @Override
    public double getDoubleValue() {
        return getFloatValue();
    }

    @Override
    public void setDoubleValue(double value, boolean setSource, boolean sync) {
        setFloatValue((float) value, setSource, sync);
    }
}
