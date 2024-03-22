package screret.sas.data.recipe.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.StrictNBTIngredient;
import screret.sas.SpellsAndSorcerers;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.recipe.ModRecipes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ShapelessWandRecipeBuilder implements RecipeBuilder {
   private final WandAbilityIngredient result;
   private final List<Ingredient> ingredients = Lists.newArrayList();
   private final Advancement.Builder advancement = Advancement.Builder.advancement();
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
      return this.requires(StrictNBTIngredient.of(pItem), 1);
   }

   public ShapelessWandRecipeBuilder requires(ItemStack pItem, int count) {
      return this.requires(StrictNBTIngredient.of(pItem), count);
   }

   /**
    * Adds the given ingredient multiple times.
    */
   public ShapelessWandRecipeBuilder requires(ItemLike pItem, int pQuantity) {
      for(int i = 0; i < pQuantity; ++i) {
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
      for(int i = 0; i < pQuantity; ++i) {
         this.ingredients.add(pIngredient);
      }

      return this;
   }

   @Override
   public ShapelessWandRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
      this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
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
   public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
      this.ensureValid(pRecipeId);
      this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
      pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.result, this.group == null ? "" : this.group, this.ingredients, this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + SpellsAndSorcerers.SAS_TAB.getRecipeFolderName() + "/" + pRecipeId.getPath())));
   }

   /**
    * Makes sure that this recipe is valid and obtainable.
    */
   private void ensureValid(ResourceLocation pId) {
      if (this.advancement.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + pId);
      }
   }

   public static class Result implements FinishedRecipe {
      private final ResourceLocation id;
      private final WandAbilityIngredient result;
      private final String group;
      private final List<Ingredient> ingredients;
      private final Advancement.Builder advancement;
      private final ResourceLocation advancementId;


      public Result(ResourceLocation pId, WandAbilityIngredient pResult, String pGroup, List<Ingredient> pIngredients, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {
         this.id = pId;
         this.result = pResult;
         this.group = pGroup;
         this.ingredients = pIngredients;
         this.advancement = pAdvancement;
         this.advancementId = pAdvancementId;
      }

      public void serializeRecipeData(JsonObject pJson) {
         if (!this.group.isEmpty()) {
            pJson.addProperty("group", this.group);
         }

         JsonArray jsonarray = new JsonArray();
         for(Ingredient ingredient : this.ingredients) {
            jsonarray.add(ingredient.toJson());
         }
         pJson.add("ingredients", jsonarray);

         JsonObject resultObject = result.toJson().getAsJsonObject();
         pJson.add("result", resultObject);
      }

      public RecipeSerializer<?> getType() {
         return ModRecipes.SHAPELESS_WAND_RECIPE_SERIALIZER.get();
      }

      /**
       * Gets the ID for the recipe.
       */
      public ResourceLocation getId() {
         return this.id;
      }

      /**
       * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
       */
      @Nullable
      public JsonObject serializeAdvancement() {
         return this.advancement.serializeToJson();
      }

      /**
       * Gets the ID for the advancement associated with this recipe. Should not be null if
       * is non-null.
       */
      @Nullable
      public ResourceLocation getAdvancementId() {
         return this.advancementId;
      }
   }
}