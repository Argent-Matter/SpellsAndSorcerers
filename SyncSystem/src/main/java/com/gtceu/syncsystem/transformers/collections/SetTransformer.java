package com.gtceu.syncsystem.transformers.collections;

import com.gtceu.syncsystem.transformers.ValueTransformer;
import com.gtceu.syncsystem.transformers.ValueTransformers;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public class SetTransformer<T> implements ValueTransformer<Set<T>> {

    private @Nullable ValueTransformer<T> elementTransformer = null;

    @SuppressWarnings("unchecked")
    private ValueTransformer<T> getElementTransformer(ValueTransformer.TransformerContext<Set<T>> context) {
        if (this.elementTransformer != null) {
            return this.elementTransformer;
        }
        Type innerType = context.type().getGenericTypeArgs()[0].getRawType();
        var transformer = (ValueTransformer<T>) ValueTransformers.get(innerType);
        if (transformer == null) {
            throw new IllegalStateException("Sync: Failed to serialize set: Missing transformer for inner type: %s"
                    .formatted(innerType));
        }
        this.elementTransformer = transformer;
        return this.elementTransformer;
    }

    private ValueTransformer.TransformerContext<T> createInnerElementContext(@Nullable T element,
                                                                             ValueTransformer.TransformerContext<Set<T>> parentContext) {
        return new TransformerContext<T>(parentContext.holder(),
                parentContext.type().getGenericTypeArgs()[0], element, parentContext.fieldName() + "[element]",
                parentContext.isClientSync(), parentContext.registries());
    }

    @Override
    public Tag serializeNBT(Set<T> value, ValueTransformer.TransformerContext<Set<T>> context) {
        ListTag tag = new ListTag();
        for (T element : value) {
            tag.add(getElementTransformer(context).serializeNBT(element, createInnerElementContext(element, context)));
        }
        return tag;
    }

    @Override
    public Set<T> deserializeNBT(Tag tag, ValueTransformer.TransformerContext<Set<T>> context) {
        ListTag listTag = ValueTransformer.assertTagType(ListTag.class, tag, context);
        Set<T> current = context.currentValue();
        if (current != null) {
            current.clear();
        } else {
            current = new ObjectOpenHashSet<>();
        }

        ValueTransformer<T> elementTransformer = getElementTransformer(context);
        for (Tag elementTag : listTag) {
            T value = elementTransformer.deserializeNBT(elementTag, createInnerElementContext(null, context));
            if (value != null) current.add(value);
        }
        return current;
    }
}
