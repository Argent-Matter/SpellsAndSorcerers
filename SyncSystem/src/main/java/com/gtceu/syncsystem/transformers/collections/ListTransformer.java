package com.gtceu.syncsystem.transformers.collections;

import com.gtceu.syncsystem.transformers.ValueTransformer;
import com.gtceu.syncsystem.transformers.ValueTransformers;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class ListTransformer<T> implements ValueTransformer<List<T>> {

    private @Nullable ValueTransformer<T> elementTransformer = null;

    @SuppressWarnings("unchecked")
    private ValueTransformer<T> getElementTransformer(ValueTransformer.TransformerContext<List<T>> context) {
        if (this.elementTransformer != null) {
            return this.elementTransformer;
        }
        var innerType = context.type().getGenericTypeArgs()[0].getRawType();
        var transformer = (ValueTransformer<T>) ValueTransformers.get(innerType);
        if (transformer == null) {
            throw new IllegalStateException("Sync: Failed to serialize list: Missing transformer for inner type: %s"
                    .formatted(innerType));
        }
        this.elementTransformer = transformer;
        return this.elementTransformer;
    }

    private ValueTransformer.TransformerContext<T> createInnerElementContext(@Nullable T element,
                                                                             ValueTransformer.TransformerContext<List<T>> parentContext) {
        return new TransformerContext<T>(parentContext.holder(),
                parentContext.type().getGenericTypeArgs()[0], element, parentContext.fieldName() + "[element]",
                parentContext.isClientSync(), parentContext.registries());
    }

    @Override
    public Tag serializeNBT(List<T> value, ValueTransformer.TransformerContext<List<T>> context) {
        ListTag list = new ListTag();
        ValueTransformer<T> elementTransformer = getElementTransformer(context);
        for (var obj : value) {
            list.add(elementTransformer.serializeNBT(obj, createInnerElementContext(obj, context)));
        }
        return list;
    }

    @Override
    public @Nullable List<T> deserializeNBT(Tag tag, ValueTransformer.TransformerContext<List<T>> context) {
        var current = context.currentValue();
        ListTag listTag = ValueTransformer.assertTagType(ListTag.class, tag, context);
        if (current != null) {
            current.clear();
        } else {
            current = new ArrayList<>();
        }

        ValueTransformer<T> elementTransformer = getElementTransformer(context);
        for (var elementTag : listTag) {
            T val = elementTransformer.deserializeNBT(ValueTransformer.stripLdlibWrapper(elementTag),
                    createInnerElementContext(null, context));
            if (val != null) current.add(val);
        }
        return current;
    }
}
