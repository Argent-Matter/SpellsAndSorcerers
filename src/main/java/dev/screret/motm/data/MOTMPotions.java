package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMPotions {

    // spotless:off
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<Potion, Potion> MANA = POTIONS.register("mana",
            () -> new Potion(new MobEffectInstance(MOTMMobEffects.MANA, 3600)));
    public static final DeferredHolder<Potion, Potion> LONG_MANA = POTIONS.register("long_mana",
            () -> new Potion(new MobEffectInstance(MOTMMobEffects.MANA, 9600)));
    public static final DeferredHolder<Potion, Potion> STRONG_MANA = POTIONS.register("strong_mana",
            () -> new Potion(new MobEffectInstance(MOTMMobEffects.MANA, 1800, 1)));

    // spotless:on

    public static void registerPotionMixes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(Potions.AWKWARD, MOTMItems.SOUL_BOTTLE.get(), MOTMPotions.MANA);
        builder.addMix(MOTMPotions.MANA, Items.REDSTONE, MOTMPotions.LONG_MANA);
        builder.addMix(MOTMPotions.MANA, Items.GLOWSTONE_DUST, MOTMPotions.STRONG_MANA);
    }
}
