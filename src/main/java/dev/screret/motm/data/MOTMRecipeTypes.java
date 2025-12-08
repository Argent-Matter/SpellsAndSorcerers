package dev.screret.motm.data;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMRecipeTypes {

    // spotless:off
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MagicOfTheMind.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MagicOfTheMind.MOD_ID);

    // spotless:on

    private static <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> simpleRecipeType(String name) {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(MOTMUtil.id(name)));
    }
}
