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

public class GenericSyncValue<B extends ByteBuf, T> extends AbstractGenericSyncValue<B, T> {

    public static GenericSyncValue<RegistryFriendlyByteBuf, ItemStack> forItem(@NotNull Supplier<ItemStack> getter,
                                                                               @Nullable Consumer<ItemStack> setter) {
        return new GenericSyncValue<>(ItemStack.class, getter, setter, ByteBufAdapters.ITEM_STACK);
    }

    public static GenericSyncValue<RegistryFriendlyByteBuf, FluidStack> forFluid(@NotNull Supplier<FluidStack> getter,
                                                                                 @Nullable Consumer<FluidStack> setter) {
        return new GenericSyncValue<>(FluidStack.class, getter, setter, ByteBufAdapters.FLUID_STACK);
    }

    private final StreamDecoder<B, T> deserializer;
    private final StreamEncoder<B, T> serializer;
    private final IEquals<T> equals;
    private final ICopy<T> copy;
    private T cache;

    public GenericSyncValue(@NotNull Class<T> type,
                            @NotNull Supplier<T> getter,
                            @Nullable Consumer<T> setter,
                            @NotNull IByteBufAdapter<B, T> adapter,
                            @Nullable ICopy<T> copy) {
        this(type, getter, setter, adapter, adapter, adapter, copy);
    }

    public GenericSyncValue(@NotNull Class<T> type,
                            @NotNull Supplier<T> getter,
                            @Nullable Consumer<T> setter,
                            @NotNull IByteBufAdapter<B, T> adapter) {
        this(type, getter, setter, adapter, adapter, adapter, null);
    }

    public GenericSyncValue(@NotNull Class<T> type,
                            @NotNull Supplier<T> getter,
                            @Nullable Consumer<T> setter,
                            @NotNull StreamDecoder<B, T> deserializer,
                            @NotNull StreamEncoder<B, T> serializer,
                            @Nullable IEquals<T> equals,
                            @Nullable ICopy<T> copy) {
        super(type, getter, setter);
        this.deserializer = Objects.requireNonNull(deserializer);
        this.serializer = Objects.requireNonNull(serializer);
        this.equals = equals == null ? Objects::equals : IEquals.wrapNullSafe(equals);
        this.copy = copy == null ? ICopy.ofSerializer(serializer, deserializer) : copy;
    }

    @Override
    protected T createDeepCopyOf(T value) {
        return this.copy.createDeepCopy(value);
    }

    @Override
    protected boolean areEqual(T a, T b) {
        return this.equals.areEqual(a, b);
    }

    @Override
    protected void serialize(B buffer, T value) {
        this.serializer.encode(buffer, value);
    }

    @Override
    protected T deserialize(B buffer) {
        return this.deserializer.decode(buffer);
    }

    @SuppressWarnings("unchecked")
    public <V> GenericSyncValue<B, V> cast() {
        return (GenericSyncValue<B, V>) this;
    }

    public static class Builder<B extends ByteBuf, T> {

        private final Class<T> type;
        private Supplier<T> getter;
        private Consumer<T> setter;
        private StreamDecoder<B, T> deserializer;
        private StreamEncoder<B, T> serializer;
        private IEquals<T> equals;
        private ICopy<T> copy;

        public Builder(Class<T> type) {
            this.type = type;
        }

        public Builder<B, T> getter(Supplier<T> getter) {
            this.getter = getter;
            return this;
        }

        public Builder<B, T> setter(Consumer<T> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<B, T> deserializer(StreamDecoder<B, T> deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public Builder<B, T> serializer(StreamEncoder<B, T> serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder<B, T> equals(IEquals<T> equals) {
            this.equals = equals;
            return this;
        }

        public Builder<B, T> equalsDefault() {
            return equals(IEquals.defaultTester());
        }

        public Builder<B, T> equalsReference() {
            return equals((a, b) -> a == b);
        }

        public Builder<B, T> copy(ICopy<T> copy) {
            this.copy = copy;
            return this;
        }

        public Builder<B, T> copyImmutable() {
            return copy(ICopy.immutable());
        }

        public Builder<B, T> adapter(IByteBufAdapter<B, T> adapter) {
            return deserializer(adapter)
                    .serializer(adapter)
                    .equals(adapter);
        }

        public GenericSyncValue<B, T> build() {
            return new GenericSyncValue<>(type, getter, setter, deserializer, serializer, equals, copy);
        }
    }
}
