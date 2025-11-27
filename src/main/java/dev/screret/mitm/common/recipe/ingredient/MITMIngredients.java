package dev.screret.mitm.common.recipe.ingredient;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import dev.screret.mitm.MagicOfTheMind;

import java.util.function.Supplier;

public class MITMIngredients {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, MagicOfTheMind.MODID);

    public static final Supplier<IngredientType<WandAbilityIngredient>> WAND_ABILITY = INGREDIENT_TYPES.register("wand_ability", () -> new IngredientType<>(WandAbilityIngredient.CODEC));
}
