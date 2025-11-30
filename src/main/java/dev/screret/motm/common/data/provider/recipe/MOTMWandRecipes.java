package dev.screret.motm.common.data.provider.recipe;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.ability.WandAbility;
import dev.screret.motm.api.ability.WandAbilityInstance;
import dev.screret.motm.common.data.builder.recipe.ShapedWandRecipeBuilder;
import dev.screret.motm.common.data.builder.recipe.ShapelessWandRecipeBuilder;
import dev.screret.motm.common.item.component.WandComponent;
import dev.screret.motm.data.MOTMDataComponents;
import dev.screret.motm.data.MOTMItems;
import dev.screret.motm.data.MOTMWandAbilities;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MOTMWandRecipes {

    protected static void buildRecipes(RecipeOutput provider) {
        MOTMUtil.generateWandItems();
        addWandUpgradeRecipes(provider);

        getShaped(MOTMWandAbilities.DAMAGE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MOTMItems.HANDLE.get())
                .define('C', getWandCore(MOTMWandAbilities.DAMAGE.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', MOTMItems.SOUL_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(MOTMWandAbilities.DAMAGE.get()))
                .save(provider, MOTMUtil.id("wand/damage"));

        getShaped(MOTMWandAbilities.EXPLODE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MOTMItems.HANDLE.get())
                .define('C', getWandCore(MOTMWandAbilities.EXPLODE.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', Items.TNT)
                .group("wands")
                .unlockedBy("has_core", hasCore(MOTMWandAbilities.EXPLODE.get()))
                .save(provider, MOTMUtil.id("wand/explosion"));

        getShaped(MOTMWandAbilities.LARGE_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MOTMItems.HANDLE.get())
                .define('C', getWandCore(MOTMWandAbilities.LARGE_FIREBALL.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', Items.FIRE_CHARGE)
                .group("wands")
                .unlockedBy("has_core", hasCore(MOTMWandAbilities.LARGE_FIREBALL.get()))
                .save(provider, MOTMUtil.id("wand/large_fireball"));

        getShaped(MOTMWandAbilities.SMALL_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MOTMItems.HANDLE.get())
                .define('C', getWandCore(MOTMWandAbilities.SMALL_FIREBALL.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', Items.FIREWORK_STAR)
                .group("wands")
                .unlockedBy("has_core", hasCore(MOTMWandAbilities.SMALL_FIREBALL.get()))
                .save(provider, MOTMUtil.id("wand/small_fireball"));

        getShaped(MOTMWandAbilities.HEAL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MOTMItems.HANDLE.get())
                .define('C', getWandCore(MOTMWandAbilities.HEAL.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING))
                .group("wands")
                .unlockedBy("has_core", hasCore(MOTMWandAbilities.HEAL.get()))
                .save(provider, MOTMUtil.id("wand/heal"));

        getShaped(MOTMWandAbilities.LIGHTNING.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MOTMItems.HANDLE.get())
                .define('C', getWandCore(MOTMWandAbilities.LIGHTNING.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', MOTMItems.CLOUD_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(MOTMWandAbilities.LIGHTNING.get()))
                .save(provider, MOTMUtil.id("wand/lightning"));
    }

    public static ShapedWandRecipeBuilder getShaped(WandAbility ability) {
        return new ShapedWandRecipeBuilder(MOTMUtil.CUSTOM_WANDS.get(ability.getKey()));
    }

    public static ShapelessWandRecipeBuilder getShapeless(WandAbility ability) {
        return new ShapelessWandRecipeBuilder(MOTMUtil.CUSTOM_WANDS.get(ability.getKey()));
    }

    public static ShapelessWandRecipeBuilder getShapeless(ItemStack wand) {
        return new ShapelessWandRecipeBuilder(wand);
    }

    public static ItemStack getWandCore(WandAbility ability) {
        return MOTMUtil.CUSTOM_WAND_CORES.get(ability.getKey());
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
        return inventoryTrigger(Arrays.stream(items).map(ItemPredicate.Builder::build)
                .<ItemPredicate>toArray(p_297943_ -> new ItemPredicate[p_297943_]));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
        return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(),
                        InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemStack stack) {
        ItemPredicate.Builder itemPredicate = ItemPredicate.Builder.item()
                .of(stack.getItem());
        if (stack.isComponentsPatchEmpty()) {
            return inventoryTrigger(itemPredicate.build());
        }
        itemPredicate.hasComponents(DataComponentPredicate.allOf(stack.getComponents()));
        return inventoryTrigger(itemPredicate.build());
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> hasCore(WandAbility ability) {
        return has(MOTMUtil.CUSTOM_WAND_CORES.get(ability.getKey()));
    }

    private static void addWandUpgradeRecipes(RecipeOutput output) {
        for (var wand : MOTMUtil.CUSTOM_WANDS.values()) {
            var result = wand.copy();
            if (!result.has(MOTMDataComponents.WAND)) {
                return;
            }
            WandComponent component = result.get(MOTMDataComponents.WAND);
            component = component.withPoweredUp(true);
            WandAbilityInstance primary = component.primary();

            getShapeless(result)
                    .requires(MOTMItems.CTHULHU_EYE.get())
                    .requires(wand)
                    .unlockedBy("has_cthulhu_eye", has(MOTMItems.CTHULHU_EYE.get()))
                    .group("wand_upgrades")
                    .save(output, MOTMUtil.id("wand_upgrade/" + primary.getId().getPath()));
        }
    }
}
