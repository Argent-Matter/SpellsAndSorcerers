package dev.screret.mitm.data;

import dev.screret.mitm.MagicOfTheMind;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MITMPotions {

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION,
            MagicOfTheMind.MODID);

    public static final DeferredHolder<Potion, Potion> MANA = POTIONS.register("mana",
            () -> new Potion(new MobEffectInstance(MITMMobEffects.MANA, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_MANA = POTIONS.register("long_mana",
            () -> new Potion(new MobEffectInstance(MITMMobEffects.MANA, 9600)));
    public static final DeferredHolder<Potion, Potion> STRONG_MANA = POTIONS.register("strong_mana",
            () -> new Potion(new MobEffectInstance(MITMMobEffects.MANA, 1800, 1)));

    public static void registerPotionMixes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, MITMItems.SOUL_BOTTLE.get(), MITMPotions.MANA);
        builder.addMix(MITMPotions.MANA, Items.REDSTONE, MITMPotions.LONG_MANA);
        builder.addMix(MITMPotions.MANA, Items.GLOWSTONE_DUST, MITMPotions.STRONG_MANA);
    }
}
