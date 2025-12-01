package dev.screret.mui.value.sync;

import dev.screret.mui.utils.ICopy;
import dev.screret.mui.utils.serialization.network.IByteBufAdapter;
import dev.screret.mui.utils.serialization.network.IEquals;

import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

import io.netty.buffer.ByteBuf;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.units.qual.C;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GenericCollectionSyncHandler<B extends ByteBuf, T, C extends Collection<T>> extends ValueSyncHandler<B, C> {

    private final Supplier<C> getter;
    private final Consumer<C> setter;
    private final StreamDecoder<B, T> decoder;
    private final StreamEncoder<B, T> encoder;
    private final IEquals<T> equals;
    private final ICopy<T> copy;

    protected GenericCollectionSyncHandler(@NotNull Supplier<C> getter,
                                           @Nullable Consumer<C> setter,
                                           @NotNull StreamDecoder<B, T> decoder,
                                           @NotNull StreamEncoder<B, T> encoder,
                                           @Nullable IEquals<T> equals,
                                           @Nullable ICopy<T> copy) {
        this.getter = getter;
        this.setter = setter;
        this.decoder = decoder;
        this.encoder = encoder;
        this.equals = equals != null ? IEquals.wrapNullSafe(equals) : Objects::equals;
        this.copy = copy != null ? copy : ICopy.ofSerializer(encoder, decoder);
    }

    @Override
    public void setValue(C value, boolean setSource, boolean sync) {
        setCache(value);
        onSetCache(value, setSource, sync);
    }

    protected abstract void setCache(C value);

    protected void onSetCache(C value, boolean setSource, boolean sync) {
        if (setSource && this.setter != null) {
            this.setter.accept(value);
        }
        if (sync) {
            // noinspection unchecked
            sync(0, buffer -> this.write((B) buffer));
        }
    }

    @Override
    public boolean updateCacheFromSource(boolean isFirstSync) {
        C c = this.getter.get();
        if (isFirstSync || didValuesChange(c)) {
            setValue(c, false, false);
            return true;
        }
        return false;
    }

    @Override
    public void notifyUpdate() {
        setValue(this.getter.get(), false, true);
    }

    protected abstract boolean didValuesChange(C newValues);

    @Override
    public void write(B buffer) {
        C c = getValue();
        VarInt.write(buffer, c.size());
        for (T t : c) {
            this.encoder.encode(buffer, t);
        }
    }

    @Override
    public abstract C getValue();

    public boolean areValuesEqual(T a, T b) {
        return this.equals.areEqual(a, b);
    }

    protected T deserializeValue(B buffer) {
        return this.decoder.decode(buffer);
    }

    protected T copyValue(T value) {
        return this.copy.createDeepCopy(value);
    }

    @Accessors(fluent = true, chain = true)
    public static class Builder<I extends ByteBuf, T, C extends Collection<T>, B extends Builder<I, T, C, B>> {

        @Setter
        protected Supplier<C> getter;
        @Setter
        protected Consumer<C> setter;
        @Setter
        protected StreamDecoder<I, T> deserializer;
        @Setter
        protected StreamEncoder<I, T> serializer;
        @Setter
        protected IEquals<T> equals;
        @Setter
        protected ICopy<T> copy;

        public B adapter(IByteBufAdapter<I, T> adapter) {
            deserializer(adapter).serializer(adapter).equals(adapter);
            return getSelf();
        }

        public B immutableCopy() {
            copy(ICopy.immutable());
            return getSelf();
        }

        @SuppressWarnings("unchecked")
        protected B getSelf() {
            return (B) this;
        }
    }
}
