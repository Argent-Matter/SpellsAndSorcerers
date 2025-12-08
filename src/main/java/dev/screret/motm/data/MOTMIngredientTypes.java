package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;

import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MOTMIngredientTypes {

    // spotless:off
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, MagicOfTheMind.MOD_ID);

    // spotless:on
}
