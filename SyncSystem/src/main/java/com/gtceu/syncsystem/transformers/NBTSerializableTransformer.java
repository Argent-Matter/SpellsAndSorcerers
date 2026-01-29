package com.gtceu.syncsystem.transformers;

import com.gtceu.syncsystem.SyncSystem;

import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import org.jetbrains.annotations.Nullable;

public class NBTSerializableTransformer<T extends Tag> implements ValueTransformer<INBTSerializable<T>> {

    private @Nullable Class<T> tagClass = null;

    @SuppressWarnings("unchecked")
    private Class<T> getSerializableTagType(ValueTransformer.TransformerContext<INBTSerializable<T>> context) {
        if (this.tagClass != null) {
            return tagClass;
        }
        Class<?> tagClass = context.type().getGenericTypeArgs()[0].getClassValue();
        if (tagClass == null) {
            throw new IllegalStateException("Sync: Failed to serialize list: Missing generic inner type: %s"
                    .formatted(context.type()));
        }
        this.tagClass = (Class<T>) tagClass;
        return this.tagClass;
    }

    @Override
    public Tag serializeNBT(INBTSerializable<T> value,
                            ValueTransformer.TransformerContext<INBTSerializable<T>> context) {
        return value.serializeNBT(context.registries());
    }

    @Override
    public @Nullable INBTSerializable<T> deserializeNBT(Tag tag,
                                                        ValueTransformer.TransformerContext<INBTSerializable<T>> context) {
        var currentVal = context.currentValue();
        if (currentVal == null) {
            SyncSystem.LOGGER.warn(
                    "Sync: Deserialization of INBTSerializable objects requires an existing object, they cannot be instantiated purely from saved data.");
            return null;
        }
        T checkedTag = ValueTransformer.assertTagType(getSerializableTagType(context), tag, context);
        currentVal.deserializeNBT(context.registries(), checkedTag);
        return currentVal;
    }
}
