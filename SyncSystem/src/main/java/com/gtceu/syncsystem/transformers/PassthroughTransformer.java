package com.gtceu.syncsystem.transformers;


import net.minecraft.nbt.Tag;

import org.jetbrains.annotations.Nullable;

public class PassthroughTransformer<T extends Tag> implements ValueTransformer<T> {

    public final Class<T> tagClass;

    public PassthroughTransformer(Class<T> tagClass) {
        this.tagClass = tagClass;
    }

    @Override
    public Tag serializeNBT(T value, TransformerContext<T> context) {
        return value;
    }

    @Override
    public @Nullable T deserializeNBT(Tag tag, TransformerContext<T> context) {
        return ValueTransformer.assertTagType(this.tagClass, tag, context);
    }
}
