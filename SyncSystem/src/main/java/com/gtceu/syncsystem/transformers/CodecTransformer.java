package com.gtceu.syncsystem.transformers;

import com.gtceu.syncsystem.SyncSystem;

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
        return codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow(false, SyncSystem.LOGGER::error);
    }

    @Override
    public T deserializeNBT(Tag tag, ValueTransformer.TransformerContext<T> context) {
        return codec.parse(NbtOps.INSTANCE, tag).getOrThrow(false, SyncSystem.LOGGER::error);
    }
}
