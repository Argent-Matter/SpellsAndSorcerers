package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MOTMAttachmentTypes {

    // spotless:off
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MagicOfTheMind.MOD_ID);

    // spotless:on
}
