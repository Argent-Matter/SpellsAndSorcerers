package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.memory.LevelMemoryStorage;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MOTMAttachmentTypes {

    // spotless:off
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<LevelMemoryStorage>> LEVEL_MEMORY_STORAGE = ATTACHMENT_TYPES.register("level_memory_storage",
            () -> AttachmentType.builder(LevelMemoryStorage::new)
                    .serialize(LevelMemoryStorage.CODEC).sync(LevelMemoryStorage.STREAM_CODEC)
                    .copyHandler(LevelMemoryStorage::copy)
                    .build());

    // spotless:on
}
