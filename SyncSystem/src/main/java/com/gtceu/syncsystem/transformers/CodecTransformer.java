package com.gtceu.syncsystem.transformers;

import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import com.mojang.serialization.Codec;

public class CodecTransformer<T> implements ValueTransformer<T> {

    private final Codec<T> codec;

    public CodecTransformer(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public Tag serializeNBT(T value, ValueTransformer.TransformerContext<T> context) {
        return codec.encodeStart(context.registries().createSerializationContext(NbtOps.INSTANCE), value).getOrThrow();
    }

    @Override
    public T deserializeNBT(Tag tag, ValueTransformer.TransformerContext<T> context) {
        return codec.parse(context.registries().createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }
}
