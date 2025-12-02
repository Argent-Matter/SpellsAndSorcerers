package dev.screret.modularui.value.sync;

import dev.screret.modularui.utils.ICopy;
import dev.screret.modularui.utils.serialization.network.IByteBufAdapter;
import dev.screret.modularui.utils.serialization.network.IEquals;

import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericMapSyncHandler<B extends ByteBuf, K, V> extends ValueSyncHandler<B, Map<K, V>> {

    private final Supplier<Map<K, V>> getter;
    private final Consumer<Map<K, V>> setter;
    private final StreamDecoder<B, K> keyDeserializer;
    private final StreamDecoder<B, V> valueDeserializer;
    private final StreamEncoder<B, K> keySerializer;
    private final StreamEncoder<B, V> valueSerializer;
    private final IEquals<V> equals;
    private final ICopy<K> keyCopy;
    private final ICopy<V> valueCopy;
    private final Map<K, V> cache = new Object2ObjectOpenHashMap<>();

    public GenericMapSyncHandler(Supplier<Map<K, V>> getter,
                                 Consumer<Map<K, V>> setter,
                                 StreamDecoder<B, K> keyDeserializer,
                                 StreamDecoder<B, V> valueDeserializer,
                                 StreamEncoder<B, K> keySerializer,
                                 StreamEncoder<B, V> valueSerializer,
                                 IEquals<V> equals,
                                 ICopy<K> keyCopy,
                                 ICopy<V> valueCopy) {
        this.getter = getter;
        this.setter = setter;
        this.keyDeserializer = keyDeserializer;
        this.valueDeserializer = valueDeserializer;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.equals = equals != null ? IEquals.wrapNullSafe(equals) : Objects::equals;
        this.keyCopy = keyCopy != null ? keyCopy : ICopy.ofSerializer(keySerializer, keyDeserializer);
        this.valueCopy = valueCopy != null ? valueCopy : ICopy.ofSerializer(valueSerializer, valueDeserializer);;
    }

    @Override
    public void setValue(Map<K, V> value, boolean setSource, boolean sync) {
        this.cache.clear();
        for (Map.Entry<K, V> entry : value.entrySet()) {
            this.cache.put(this.keyCopy.createDeepCopy(entry.getKey()),
                    this.valueCopy.createDeepCopy(entry.getValue()));
        }
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
        Map<K, V> map = this.getter.get();
        if (isFirstSync || didValuesChange(map)) {
            setValue(map, false, false);
            return true;
        }
        return false;
    }

    @Override
    public void notifyUpdate() {
        setValue(this.getter.get(), false, true);
    }

    protected boolean didValuesChange(Map<K, V> value) {
        if (this.cache.size() != value.size()) return true;
        for (Map.Entry<K, V> entry : this.cache.entrySet()) {
            if (!value.containsKey(entry.getKey())) return true;
            V v = value.get(entry.getKey());
            if (!this.equals.areEqual(entry.getValue(), v)) return true;
        }
        return false;
    }

    @Override
    public void write(B buffer) {
        VarInt.write(buffer, this.cache.size());
        for (Map.Entry<K, V> entry : this.cache.entrySet()) {
            this.keySerializer.encode(buffer, entry.getKey());
            this.valueSerializer.encode(buffer, entry.getValue());
        }
    }

    @Override
    public void read(B buffer) {
        this.cache.clear();
        int size = VarInt.read(buffer);
        for (int i = 0; i < size; i++) {
            this.cache.put(this.keyDeserializer.decode(buffer), this.valueDeserializer.decode(buffer));
        }
        this.setter.accept(getValue());
    }

    @Override
    public Map<K, V> getValue() {
        return Collections.unmodifiableMap(this.cache);
    }

    @Accessors(fluent = true, chain = true)
    public static class Builder<B extends ByteBuf, K, V> {

        @Setter
        private Supplier<Map<K, V>> getter;
        @Setter
        private Consumer<Map<K, V>> setter;
        @Setter
        private StreamDecoder<B, K> keyDeserializer;
        @Setter
        private StreamDecoder<B, V> valueDeserializer;
        @Setter
        private StreamEncoder<B, K> keySerializer;
        @Setter
        private StreamEncoder<B, V> valueSerializer;
        @Setter
        private IEquals<V> equals;
        @Setter
        private ICopy<K> keyCopy;
        @Setter
        private ICopy<V> valueCopy;

        public Builder<B, K, V> keyAdapter(IByteBufAdapter<B, K> adapter) {
            return keyDeserializer(adapter).keySerializer(adapter);
        }

        public Builder<B, K, V> valueAdapter(IByteBufAdapter<B, V> adapter) {
            return valueDeserializer(adapter).valueSerializer(adapter).equals(adapter);
        }

        public Builder<B, K, V> immutableKey() {
            return keyCopy(ICopy.immutable());
        }

        public Builder<B, K, V> immutableValue() {
            return valueCopy(ICopy.immutable());
        }

        public GenericMapSyncHandler<B, K, V> build() {
            if (this.getter == null) {
                throw new NullPointerException("Getter in GenericMapSyncHandler must not be null");
            }
            if (this.keyDeserializer == null) {
                throw new NullPointerException("Key deserializer in GenericMapSyncHandler must not be null");
            }
            if (this.valueDeserializer == null) {
                throw new NullPointerException("Value deserializer in GenericMapSyncHandler must not be null");
            }
            if (this.keySerializer == null) {
                throw new NullPointerException("Key serializer in GenericMapSyncHandler must not be null");
            }
            if (this.valueSerializer == null) {
                throw new NullPointerException("Value serializer in GenericMapSyncHandler must not be null");
            }
            return new GenericMapSyncHandler<>(this.getter, this.setter, this.keyDeserializer, this.valueDeserializer,
                    this.keySerializer,
                    this.valueSerializer, this.equals, this.keyCopy, this.valueCopy);
        }
    }
}
