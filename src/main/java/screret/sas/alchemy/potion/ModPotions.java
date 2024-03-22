package screret.sas.alchemy.potion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.alchemy.effect.ModMobEffects;
import screret.sas.item.ModItems;
import screret.sas.mixin.PotionBrewingAccessor;

import java.util.function.Supplier;

public class ModPotions {

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, SpellsAndSorcerers.MODID);

    public static final Supplier<Potion> MANA = POTIONS.register("mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 3600)));
    public static final Supplier<Potion> LONG_MANA = POTIONS.register("long_mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 9600)));
    public static final Supplier<Potion> STRONG_MANA = POTIONS.register("strong_mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 1800, 1)));

    public static void registerPotionMixes() {
        PotionBrewingAccessor.invokeAddMix(Potions.AWKWARD, ModItems.SOUL_BOTTLE.get(), ModPotions.MANA.get());
        PotionBrewingAccessor.invokeAddMix(ModPotions.MANA.get(), Items.REDSTONE, ModPotions.LONG_MANA.get());
        PotionBrewingAccessor.invokeAddMix(ModPotions.MANA.get(), Items.GLOWSTONE_DUST, ModPotions.STRONG_MANA.get());
    }
}

