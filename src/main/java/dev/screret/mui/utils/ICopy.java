package dev.screret.mui.utils;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.neoforged.neoforge.network.connection.ConnectionType;

import dev.screret.mui.utils.serialization.network.IByteBufAdapter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public interface ICopy<T> {

    static <T> ICopy<T> immutable() {
        return t -> t;
    }

    @SuppressWarnings("unchecked")
    static <B extends ByteBuf, T> ICopy<T> ofSerializer(StreamEncoder<B, T> serializer, StreamDecoder<B, T> deserializer) {
        return t -> {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(),
                    RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), ConnectionType.NEOFORGE);
            serializer.encode((B) buf, t);
            return deserializer.decode((B) buf);
        };
    }

    static <B extends ByteBuf, T> ICopy<T> ofSerializer(IByteBufAdapter<B, T> adapter) {
        return ofSerializer(adapter, adapter);
    }

    T createDeepCopy(T t);
}
