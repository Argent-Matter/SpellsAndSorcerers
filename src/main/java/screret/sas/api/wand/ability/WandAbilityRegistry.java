package screret.sas.api.wand.ability;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;

import java.util.function.Supplier;

public class WandAbilityRegistry {
    public static final DeferredRegister<WandAbility> WAND_ABILITIES = DeferredRegister.create(Util.id("wand_abilities"), SpellsAndSorcerers.MODID);

    public static Supplier<IForgeRegistry<WandAbility>> WAND_ABILITIES_BUILTIN = WAND_ABILITIES.makeRegistry(() -> new RegistryBuilder<WandAbility>().setDefaultKey(Util.id("dummy")));

    public static void init() {}
}
