package dev.screret.mui.utils;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSources;
import net.neoforged.neoforge.common.conditions.ICondition;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@Accessors(fluent = true)
public final class RegistryAccessContainer implements ICondition.IContext, RegistryAccess.Frozen {

    public static final RegistryAccessContainer BUILTIN = new RegistryAccessContainer(
            RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

    @Getter
    private static RegistryAccessContainer current = BUILTIN;
    private static ICondition.IContext tagContext = ICondition.IContext.TAGS_INVALID;

    @Getter
    private final RegistryAccess.Frozen access;
    private DamageSources damageSources;

    public RegistryAccessContainer(RegistryAccess.Frozen access) {
        this.access = access;
        this.damageSources = null;
    }

    public DamageSources damageSources() {
        if (damageSources == null) {
            damageSources = new DamageSources(access);
        }

        return damageSources;
    }

    @ApiStatus.Internal
    public static void update(RegistryAccess.Frozen registries, @Nullable ICondition.IContext tagContext) {
        RegistryAccessContainer.current = new RegistryAccessContainer(registries);
        if (tagContext != null) {
            RegistryAccessContainer.tagContext = tagContext;
        }
    }

    // region tag context methods

    @Override
    public <T> Collection<Holder<T>> getTag(TagKey<T> key) {
        return tagContext.getTag(key);
    }

    @Override
    public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
        return tagContext.getAllTags(registry);
    }

    // endregion

    // region registry access methods

    @Override
    public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> registryKey) {
        return access.registry(registryKey);
    }

    @Override
    public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
        return access.lookup(registryKey);
    }

    @Override
    public <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> registryKey) {
        return access.registryOrThrow(registryKey);
    }

    @Override
    public Stream<RegistryEntry<?>> registries() {
        return access.registries();
    }

    @Override
    public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
        return access.listRegistries();
    }

    // endregion
}
