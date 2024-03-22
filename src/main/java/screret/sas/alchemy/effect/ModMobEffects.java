package screret.sas.alchemy.effect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.alchemy.effect.effect.ManaMobEffect;

import java.util.function.Supplier;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SpellsAndSorcerers.MODID);

    public static final Supplier<MobEffect> MANA = EFFECTS.register("mana", () -> new ManaMobEffect(MobEffectCategory.BENEFICIAL, 0x00e180));
}
