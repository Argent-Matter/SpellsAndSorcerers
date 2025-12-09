package dev.screret.motm.api.registry;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.MemoryScene;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = MagicOfTheMind.MOD_ID)
public class MOTMRegistries {

    public static final ResourceKey<Registry<MemoryScene>> MEMORY_SCENE_REGISTRY = ResourceKey
            .createRegistryKey(MOTMUtil.id("memory_scene"));

    @SubscribeEvent
    public static void registerStaticRegistries(NewRegistryEvent event) {}

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {}

}
