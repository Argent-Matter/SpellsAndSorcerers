package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.capability.mana.Mana;
import dev.screret.motm.config.MOTMConfig;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class MOTMAttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MagicOfTheMind.MODID);

    public static final Supplier<AttachmentType<Mana>> MANA = ATTACHMENT_TYPES.register("mana",
            () -> AttachmentType.serializable(() -> new Mana(
                    MOTMConfig.Server.maxDefaultMana.get(),
                    MOTMConfig.Server.maxDefaultMana.get(),
                    MOTMConfig.Server.maxDefaultMana.get(),
                    MOTMConfig.Server.maxDefaultMana.get())).build());
}
