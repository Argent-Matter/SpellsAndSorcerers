package screret.sas.attachmenttypes;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import screret.sas.SpellsAndSorcerers;
import screret.sas.api.capability.mana.CapabilityMana;
import screret.sas.config.SASConfig;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, SpellsAndSorcerers.MODID);

    public static final Supplier<AttachmentType<CapabilityMana>> MANA = ATTACHMENT_TYPES.register("mana", () -> AttachmentType.serializable(() -> new CapabilityMana(SASConfig.Server.maxDefaultMana.get(), SASConfig.Server.maxDefaultMana.get(), SASConfig.Server.maxDefaultMana.get(), SASConfig.Server.maxDefaultMana.get())).build());
}
