package dev.screret.mitm.data;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.api.capability.mana.Mana;
import dev.screret.mitm.config.MITMConfig;

import java.util.function.Supplier;

public class MITMAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MagicOfTheMind.MODID);

    public static final Supplier<AttachmentType<Mana>> MANA = ATTACHMENT_TYPES.register("mana",
            () -> AttachmentType.serializable(() -> new Mana(
                    MITMConfig.Server.maxDefaultMana.get(),
                    MITMConfig.Server.maxDefaultMana.get(),
                            MITMConfig.Server.maxDefaultMana.get(),
                            MITMConfig.Server.maxDefaultMana.get()
                    )
            ).build());
}
