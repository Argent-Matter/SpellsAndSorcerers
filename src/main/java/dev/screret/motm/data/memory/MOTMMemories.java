package dev.screret.motm.data.memory;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.api.registry.MOTMRegistries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class MOTMMemories {

    public static final ResourceKey<Memory> TEST_1 = createMemoryKey("test_1");

    public static void bootstrap(BootstrapContext<Memory> context) {
        registerMemory(context, TEST_1);
    }

    // region utility functions

    private static void registerMemory(BootstrapContext<Memory> context, ResourceKey<Memory> key) {
        registerMemory(context, key, key.location());
    }

    private static void registerMemory(BootstrapContext<Memory> context, ResourceKey<Memory> key,
                                       @Nullable ResourceLocation initialStructure) {
        context.register(key, new Memory(key.location(), Optional.ofNullable(initialStructure)));
    }

    private static ResourceKey<Memory> createMemoryKey(String name) {
        return ResourceKey.create(MOTMRegistries.MEMORY_REGISTRY, MOTMUtil.id(name));
    }

    // endregion
}
