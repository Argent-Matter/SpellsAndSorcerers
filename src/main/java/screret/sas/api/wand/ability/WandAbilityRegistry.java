package screret.sas.api.wand.ability;

import net.minecraft.core.Registry;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;

public class WandAbilityRegistry {
    public static final DeferredRegister<WandAbility> WAND_ABILITIES = DeferredRegister.create(Util.id("wand_abilities"), SpellsAndSorcerers.MODID);

    public static Registry<WandAbility> WAND_ABILITIES_BUILTIN = WAND_ABILITIES.makeRegistry((builder) -> builder.defaultKey(Util.id("dummy")));

    public static void init() {
    }
}
