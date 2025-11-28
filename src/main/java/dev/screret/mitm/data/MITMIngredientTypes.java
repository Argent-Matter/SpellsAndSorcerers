package dev.screret.mitm.data;

import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.common.recipe.ingredient.WandAbilityIngredient;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class MITMIngredientTypes {

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, MagicOfTheMind.MODID);

    public static final Supplier<IngredientType<WandAbilityIngredient>> WAND_ABILITY = INGREDIENT_TYPES.register("wand_ability",
            () -> new IngredientType<>(WandAbilityIngredient.CODEC));
}
