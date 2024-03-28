package screret.sas.data.recipe.builder;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
import screret.sas.SpellsAndSorcerers;
import screret.sas.recipe.ModRecipeTypes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;
import screret.sas.recipe.recipe.ShapelessWandRecipe;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShapelessWandRecipeBuilder implements RecipeBuilder {
    private final WandAbilityIngredient result;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;

    public ShapelessWandRecipeBuilder(ItemStack pResult) {
        this.result = WandAbilityIngredient.fromStack(pResult);
    }

    /**
     * Adds an ingredient that can be any item in the given tag.
     */
    public ShapelessWandRecipeBuilder requires(TagKey<Item> pTag) {
        return this.requires(Ingredient.of(pTag));
    }

    /**
     * Adds an ingredient of the given item.
     */
    public ShapelessWandRecipeBuilder requires(ItemLike pItem) {
        return this.requires(pItem, 1);
    }

    public ShapelessWandRecipeBuilder requires(ItemStack pItem) {
        return this.requires(NBTIngredient.of(true, pItem), 1);
    }

    public ShapelessWandRecipeBuilder requires(ItemStack pItem, int count) {
        return this.requires(NBTIngredient.of(true, pItem), count);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public ShapelessWandRecipeBuilder requires(ItemLike pItem, int pQuantity) {
        for (int i = 0; i < pQuantity; ++i) {
            this.requires(Ingredient.of(pItem));
        }

        return this;
    }

    /**
     * Adds an ingredient.
     */
    public ShapelessWandRecipeBuilder requires(Ingredient pIngredient) {
        return this.requires(pIngredient, 1);
    }

    /**
     * Adds an ingredient multiple times.
     */
    public ShapelessWandRecipeBuilder requires(Ingredient pIngredient, int pQuantity) {
        for (int i = 0; i < pQuantity; ++i) {
            this.ingredients.add(pIngredient);
        }

        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        this.criteria.put(pName, pCriterion);
        return this;
    }

    @Override
    public ShapelessWandRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getStack().getItem();
    }

    @Override
    public void save(RecipeOutput pRecipeOutput, ResourceLocation pId) {
        this.ensureValid(pId);
        var advancement = pRecipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pId)).rewards(AdvancementRewards.Builder.recipe(pId)).requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        pRecipeOutput.accept(pId, new ShapelessWandRecipe(this.group, this.ingredients, this.result), advancement.build(new ResourceLocation(pId.getNamespace(), "recipes/" + pId.getPath())));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + pId);
        }
    }
}