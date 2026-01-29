package com.gtceu.syncsystem.transformers.collections;

import com.gtceu.syncsystem.transformers.ValueTransformer;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ObjectArrayTransformer<T> implements ValueTransformer<T[]> {

    private final ValueTransformer<T> elementTransformer;

    public ObjectArrayTransformer(ValueTransformer<T> elementTransformer) {
        this.elementTransformer = elementTransformer;
    }

    private ValueTransformer.TransformerContext<T> createInnerElementContext(@Nullable T element,
                                                                             ValueTransformer.TransformerContext<T[]> parentContext) {
        return new TransformerContext<T>(parentContext.holder(),
                parentContext.type().getArrayComponentType(), element, parentContext.fieldName() + "[element]",
                parentContext.isClientSync(), parentContext.registries());
    }

    @Override
    public Tag serializeNBT(T[] value, ValueTransformer.TransformerContext<T[]> context) {
        ListTag listTag = new ListTag();
        for (T element : value) {
            listTag.add(this.elementTransformer.serializeNBT(element, createInnerElementContext(element, context)));
        }
        return listTag;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable T @Nullable [] deserializeNBT(Tag tag, ValueTransformer.TransformerContext<T[]> context) {
        T[] current = context.currentValue();
        ListTag listTag = ValueTransformer.assertTagType(ListTag.class, tag, context);

        if (current == null) {
            current = (T[]) Array.newInstance((Class<T>) (context.type().getArrayComponentType().getRawType()),
                    listTag.size());
        }

        if (listTag.size() != current.length) {
            current = Arrays.copyOf(current, listTag.size());
        }
        for (int i = 0; i < listTag.size(); i++) {
            var currentV = current[i];
            T result = this.elementTransformer.deserializeNBT(ValueTransformer.stripLdlibWrapper(listTag.get(i)),
                    createInnerElementContext(null, context));
            if (result == null) return current;
            if (result != currentV) current[i] = result;
        }
        return current;
    }
}
