package com.gtceu.syncsystem.transformers;

import com.gtceu.syncsystem.SyncSystem;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

public class RegistryValueLikeTransformer<T> implements ValueTransformer<T> {

    private static final String NOT_A_REGISTRY = "[non-registry lookup]";

    private final Function<T, @Nullable ResourceLocation> getId;
    private final Function<ResourceLocation, @Nullable T> getValue;
    private final @Nullable String registryName;

    public RegistryValueLikeTransformer(Function<T, @Nullable ResourceLocation> getId,
                                        Function<ResourceLocation, @Nullable T> getValue) {
        this.getId = getId;
        this.getValue = getValue;
        this.registryName = NOT_A_REGISTRY;
    }

    public RegistryValueLikeTransformer(Registry<T> registry) {
        this.getId = registry::getKey;
        this.getValue = registry::get;
        this.registryName = registry.key().location().toString();
    }

    @Override
    public Tag serializeNBT(T value, ValueTransformer.TransformerContext<T> context) {
        ResourceLocation id = getId.apply(value);
        if (id == null) {
            SyncSystem.LOGGER.error("Unknown value in registry {}: {}", this.registryName, value);
            return new CompoundTag();
        }
        return StringTag.valueOf(id.toString());
    }

    @Override
    public @Nullable T deserializeNBT(Tag tag, ValueTransformer.TransformerContext<T> context) {
        ResourceLocation id = ResourceLocation
                .tryParse(ValueTransformer.assertTagType(StringTag.class, tag, context).getAsString());
        if (id == null) {
            SyncSystem.LOGGER.error("Invalid registry key {}", tag.getAsString());
            return null;
        }
        T value = getValue.apply(id);
        if (value == null) {
            SyncSystem.LOGGER.error("Unknown key in registry {}: {}", this.registryName, id);
        }
        return getValue.apply(id);
    }
}
