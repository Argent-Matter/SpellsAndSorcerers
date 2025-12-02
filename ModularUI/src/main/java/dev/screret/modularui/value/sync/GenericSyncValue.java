package dev.screret.modularui.value.sync;

import dev.screret.modularui.utils.ICopy;
import dev.screret.modularui.utils.serialization.network.ByteBufAdapters;
import dev.screret.modularui.utils.serialization.network.IByteBufAdapter;
import dev.screret.modularui.utils.serialization.network.IEquals;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import io.netty.buffer.ByteBuf;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericSyncValue<B extends ByteBuf, T> extends ValueSyncHandler<B, T> {

    public static GenericSyncValue<RegistryFriendlyByteBuf, ItemStack> forItem(@NotNull Supplier<ItemStack> getter,
                                                                               @Nullable Consumer<ItemStack> setter) {
        return new GenericSyncValue<>(getter, setter, ByteBufAdapters.ITEM_STACK);
    }

    public static GenericSyncValue<RegistryFriendlyByteBuf, FluidStack> forFluid(@NotNull Supplier<FluidStack> getter,
                                                                                 @Nullable Consumer<FluidStack> setter) {
        return new GenericSyncValue<>(getter, setter, ByteBufAdapters.FLUID_STACK);
    }

    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final StreamDecoder<B, T> decoder;
    private final StreamEncoder<B, T> encoder;
    private final IEquals<T> equals;
    private final ICopy<T> copy;
    private T cache;

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @Nullable Consumer<T> setter,
                            @NotNull IByteBufAdapter<B, T> adapter) {
        this(getter, setter, adapter, adapter, adapter, null);
    }

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @Nullable Consumer<T> setter,
                            @NotNull IByteBufAdapter<B, T> adapter,
                            @Nullable ICopy<T> copy) {
        this(getter, setter, adapter, adapter, adapter, copy);
    }

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @Nullable Consumer<T> setter,
                            @NotNull StreamDecoder<B, T> decoder,
                            @NotNull StreamEncoder<B, T> encoder) {
        this(getter, setter, decoder, encoder, null, null);
    }

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @Nullable Consumer<T> setter,
                            @NotNull StreamDecoder<B, T> decoder,
                            @NotNull StreamEncoder<B, T> encoder,
                            @Nullable ICopy<T> copy) {
        this(getter, setter, decoder, encoder, null, copy);
    }

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @NotNull IByteBufAdapter<B, T> adapter) {
        this(getter, null, adapter, adapter, adapter, null);
    }

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @NotNull IByteBufAdapter<B, T> adapter,
                            @Nullable ICopy<T> copy) {
        this(getter, null, adapter, adapter, adapter, copy);
    }

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @NotNull StreamDecoder<B, T> decoder,
                            @NotNull StreamEncoder<B, T> encoder) {
        this(getter, null, decoder, encoder, null, null);
    }

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @NotNull StreamDecoder<B, T> decoder,
                            @NotNull StreamEncoder<B, T> encoder,
                            @Nullable ICopy<T> copy) {
        this(getter, null, decoder, encoder, null, copy);
    }

    public GenericSyncValue(@NotNull Supplier<T> getter,
                            @Nullable Consumer<T> setter,
                            @NotNull StreamDecoder<B, T> decoder,
                            @NotNull StreamEncoder<B, T> encoder,
                            @Nullable IEquals<T> equals,
                            @Nullable ICopy<T> copy) {
        this.getter = Objects.requireNonNull(getter);
        this.cache = getter.get();
        this.setter = setter;
        this.decoder = Objects.requireNonNull(decoder);
        this.encoder = Objects.requireNonNull(encoder);
        this.equals = equals == null ? Objects::equals : IEquals.wrapNullSafe(equals);
        this.copy = copy == null ? ICopy.ofSerializer(encoder, decoder) : copy;
    }

    @Override
    public T getValue() {
        return this.cache;
    }

    @Override
    public void setValue(T value, boolean setSource, boolean sync) {
        this.cache = this.copy.createDeepCopy(value);
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
        T t = this.getter.get();
        if (isFirstSync || !this.equals.areEqual(this.cache, t)) {
            setValue(t, false, false);
            return true;
        }
        return false;
    }

    @Override
    public void notifyUpdate() {
        setValue(this.getter.get(), false, true);
    }

    @Override
    public void write(B buffer) {
        this.encoder.encode(buffer, this.cache);
    }

    @Override
    public void read(B buffer) {
        setValue(this.decoder.decode(buffer), true, false);
    }

    @SuppressWarnings("unchecked")
    public @Nullable Class<? extends T> getType() {
        if (this.cache != null) {
            return (Class<? extends T>) this.cache.getClass();
        }
        T t = this.getter.get();
        if (t != null) {
            return (Class<? extends T>) t.getClass();
        }
        return null;
    }

    public boolean isOfType(Class<?> expectedType) {
        Class<? extends T> type = getType();
        if (type == null) {
            throw new IllegalStateException("Could not infer type of GenericSyncValue since value is null!");
        }
        return expectedType.isAssignableFrom(type);
    }

    @SuppressWarnings("unchecked")
    public <I extends ByteBuf, V> GenericSyncValue<I, V> cast() {
        return (GenericSyncValue<I, V>) this;
    }
}
