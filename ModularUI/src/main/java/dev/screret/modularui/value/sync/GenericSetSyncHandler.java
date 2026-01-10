package dev.screret.modularui.value.sync;

import dev.screret.modularui.utils.ICopy;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericSetSyncHandler<B extends ByteBuf, T> extends GenericCollectionSyncHandler<B, T, Set<T>> {

    private final Set<T> cache = new ObjectOpenHashSet<T>();

    public GenericSetSyncHandler(@NotNull Supplier<Set<T>> getter, @Nullable Consumer<Set<T>> setter,
                                 @NotNull StreamDecoder<B, T> deserializer,
                                 @NotNull StreamEncoder<B, T> serializer,
                                 @Nullable ICopy<T> copy) {
        super(getter, setter, deserializer, serializer, null, copy);
        setCache(getter.get());
    }

    @Override
    protected void setCache(Set<T> value) {
        this.cache.clear();
        for (T item : value) {
            this.cache.add(copyValue(item));
        }
    }

    @Override
    protected boolean didValuesChange(Set<T> newValues) {
        if (this.cache.size() != newValues.size()) return true;
        return cache.containsAll(newValues);
    }

    @Override
    public Set<T> getValue() {
        return Collections.unmodifiableSet(this.cache);
    }

    @Override
    public void read(B buffer) {
        this.cache.clear();
        int size = VarInt.read(buffer);
        for (int i = 0; i < size; i++) {
            this.cache.add(deserializeValue(buffer));
        }
        onSetCache(getValue(), true, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<Set<T>> getValueType() {
        return (Class<Set<T>>) (Object) Set.class;
    }

    public static <B extends ByteBuf, T> Builder<B, T> builder() {
        return new Builder<>();
    }

    public static class Builder<B extends ByteBuf, T> extends GenericCollectionSyncHandler.Builder<B, T, Set<T>, Builder<B, T>> {

        public GenericSetSyncHandler<B, T> build() {
            if (this.getter == null) throw new NullPointerException("Getter in GenericSetSyncHandler must not be null");
            if (this.deserializer == null)
                throw new NullPointerException("Deserializer in GenericSetSyncHandler must not be null");
            if (this.serializer == null)
                throw new NullPointerException("Serializer in GenericSetSyncHandler must not be null");
            return new GenericSetSyncHandler<>(this.getter, this.setter, this.deserializer, this.serializer, this.copy);
        }
    }
}
