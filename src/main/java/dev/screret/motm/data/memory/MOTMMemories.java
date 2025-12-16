package dev.screret.motm.data.memory;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.api.registry.MOTMRegistries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class MOTMMemories {

    public static final ResourceKey<Memory> TEST_1 = createMemoryKey("test_1");

    public static void bootstrap(BootstrapContext<Memory> context) {
        registerSimple(context, TEST_1);
    }

    // region utility functions

    private static void registerSimple(BootstrapContext<Memory> context, ResourceKey<Memory> key) {
        ResourceLocation name = key.location();
        context.register(key, new Memory(name, name, Optional.of(name)));
    }

    private static ResourceKey<Memory> createMemoryKey(String name) {
        return ResourceKey.create(MOTMRegistries.MEMORY_REGISTRY, MOTMUtil.id(name));
    }

    // endregion
}
