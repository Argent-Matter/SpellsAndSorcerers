package screret.sas.recipe.ingredient;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import screret.sas.SpellsAndSorcerers;

import java.util.function.Supplier;

public class ModIngredients {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, SpellsAndSorcerers.MODID);

    public static final Supplier<IngredientType<WandAbilityIngredient>> WAND_ABILITY = INGREDIENT_TYPES.register("wand_ability", () -> new IngredientType<>(WandAbilityIngredient.CODEC));
}
