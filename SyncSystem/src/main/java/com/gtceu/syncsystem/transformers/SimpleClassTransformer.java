package com.gtceu.syncsystem.transformers;

import net.minecraft.nbt.Tag;

import java.util.function.Function;

public class SimpleClassTransformer<T, S extends Tag> implements ValueTransformer<T> {

    private final Function<T, S> writer;
    private final Function<S, T> reader;
    private final Class<S> tagClass;

    public SimpleClassTransformer(Function<T, S> writer, Function<S, T> reader, Class<S> tagClass) {
        this.writer = writer;
        this.reader = reader;
        this.tagClass = tagClass;
    }

    @Override
    public Tag serializeNBT(T value, ValueTransformer.TransformerContext<T> context) {
        return writer.apply(value);
    }

    @Override
    public T deserializeNBT(Tag tag, ValueTransformer.TransformerContext<T> context) {
        S t = ValueTransformer.assertTagType(tagClass, tag, context);
        return reader.apply(t);
    }
}
