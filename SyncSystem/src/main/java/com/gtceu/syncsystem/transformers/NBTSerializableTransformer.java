package com.gtceu.syncsystem.transformers;

import com.gtceu.syncsystem.SyncSystem;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class NBTSerializableTransformer implements ValueTransformer<INBTSerializable<Tag>> {

    @Override
    public Tag serializeNBT(INBTSerializable<Tag> value,
                            ValueTransformer.TransformerContext<INBTSerializable<Tag>> context) {
        return value.serializeNBT();
    }

    @Override
    public @Nullable INBTSerializable<Tag> deserializeNBT(Tag tag,
                                                          ValueTransformer.TransformerContext<INBTSerializable<Tag>> context) {
        var currentVal = context.currentValue();
        if (currentVal == null) {
            SyncSystem.LOGGER.warn(
                    "Sync: Deserialization of INBTSerializable objects requires an existing object, they cannot be instantiated purely from saved data.");
            return null;
        }
        currentVal.deserializeNBT(tag);
        return currentVal;
    }
}
