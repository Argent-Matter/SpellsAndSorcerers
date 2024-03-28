package screret.sas.data.recipe.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
import screret.sas.SpellsAndSorcerers;
import screret.sas.creativetab.ModCreativeTabs;
import screret.sas.recipe.ModRecipeTypes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;
import screret.sas.recipe.recipe.ShapedWandRecipe;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class ShapedWandRecipeBuilder implements RecipeBuilder {
    private final WandAbilityIngredient result;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    @Nullable
    private String group;

    public ShapedWandRecipeBuilder(WandAbilityIngredient pResult) {
        this.result = pResult;
    }

    public ShapedWandRecipeBuilder(ItemStack pResult) {
        this.result = WandAbilityIngredient.fromStack(pResult);
    }


    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedWandRecipeBuilder define(Character pSymbol, TagKey<Item> pTag) {
        return this.define(pSymbol, Ingredient.of(pTag));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedWandRecipeBuilder define(Character symbol, ItemStack stack) {
        return this.define(symbol, NBTIngredient.of(true, stack));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedWandRecipeBuilder define(Character pSymbol, ItemLike pItem) {
        return this.define(pSymbol, Ingredient.of(pItem));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedWandRecipeBuilder define(Character pSymbol, Ingredient pIngredient) {
        if (this.key.containsKey(pSymbol)) {
            throw new IllegalArgumentException("Symbol '" + pSymbol + "' is already defined!");
        } else if (pSymbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(pSymbol, pIngredient);
            return this;
        }
    }

    /**
     * Adds a new entry to the patterns for this recipe.
     */
    public ShapedWandRecipeBuilder pattern(String pPattern) {
        if (!this.rows.isEmpty() && pPattern.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pPattern);
            return this;
        }
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        this.criteria.put(pName, pCriterion);
        return this;
    }

    @Override
    public ShapedWandRecipeBuilder group(@Nullable String pGroupName) {
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
        pRecipeOutput.accept(pId, new ShapedWandRecipe(this.group, ShapedRecipePattern.of(this.key, this.rows), this.result), advancement.build(new ResourceLocation(pId.getNamespace(), "recipes/" + pId.getPath())));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation pId) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + pId + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for (String s : this.rows) {
                for (int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + pId + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + pId);
            } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + pId + " only takes in a single item - should it be a shapeless recipe instead?");
            } else if (this.criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + pId);
            }
        }
    }
}