package dev.screret.motm.api.registry;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.MemoryStructure;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.api.memory.animation.MemoryKeyframe;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = MagicOfTheMind.MOD_ID)
public class MOTMRegistries {

    // spotless:off
    public static final ResourceKey<Registry<MemoryStructure>> MEMORY_STRUCTURE_REGISTRY = ResourceKey.createRegistryKey(MOTMUtil.id("memory/structure"));
    public static final ResourceKey<Registry<DataComponentType<? extends MemoryKeyframe>>> MEMORY_ANIMATION_KEYFRAME_TYPE_REGISTRY = ResourceKey.createRegistryKey(MOTMUtil.id("memory/animation_keyframe_type"));
    public static final ResourceKey<Registry<Memory>> MEMORY_REGISTRY = ResourceKey.createRegistryKey(MOTMUtil.id("memory"));


    public static final Registry<DataComponentType<? extends MemoryKeyframe>> MEMORY_ANIMATION_KEYFRAME_TYPES = new RegistryBuilder<>(MEMORY_ANIMATION_KEYFRAME_TYPE_REGISTRY)
            .create();
    // spotless:on

    @SubscribeEvent
    public static void registerStaticRegistries(NewRegistryEvent event) {
        event.register(MEMORY_ANIMATION_KEYFRAME_TYPES);
    }

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MEMORY_STRUCTURE_REGISTRY, MemoryStructure.DIRECT_CODEC, MemoryStructure.DIRECT_CODEC);
        event.dataPackRegistry(MEMORY_REGISTRY, Memory.DIRECT_CODEC, Memory.DIRECT_CODEC);
    }
}
