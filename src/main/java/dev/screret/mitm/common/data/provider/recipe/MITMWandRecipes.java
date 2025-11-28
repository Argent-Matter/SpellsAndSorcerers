package dev.screret.mitm.common.data.provider.recipe;

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
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.item.component.WandComponent;
import dev.screret.mitm.data.MITMDataComponents;
import dev.screret.mitm.data.MITMWandAbilities;
import dev.screret.mitm.api.ability.WandAbility;
import dev.screret.mitm.api.ability.WandAbilityInstance;
import dev.screret.mitm.common.data.builder.recipe.ShapedWandRecipeBuilder;
import dev.screret.mitm.common.data.builder.recipe.ShapelessWandRecipeBuilder;
import dev.screret.mitm.data.MITMItems;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MITMWandRecipes {
    protected static void buildRecipes(RecipeOutput provider) {
        MITMUtil.generateWandItems();
        addWandUpgradeRecipes(provider);

        getShaped(MITMWandAbilities.DAMAGE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MITMItems.HANDLE.get())
                .define('C', getWandCore(MITMWandAbilities.DAMAGE.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', MITMItems.SOUL_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(MITMWandAbilities.DAMAGE.get()))
                .save(provider, MITMUtil.id("wand/damage"));

        getShaped(MITMWandAbilities.EXPLODE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MITMItems.HANDLE.get())
                .define('C', getWandCore(MITMWandAbilities.EXPLODE.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', Items.TNT)
                .group("wands")
                .unlockedBy("has_core", hasCore(MITMWandAbilities.EXPLODE.get()))
                .save(provider, MITMUtil.id("wand/explosion"));

        getShaped(MITMWandAbilities.LARGE_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MITMItems.HANDLE.get())
                .define('C', getWandCore(MITMWandAbilities.LARGE_FIREBALL.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', Items.FIRE_CHARGE)
                .group("wands")
                .unlockedBy("has_core", hasCore(MITMWandAbilities.LARGE_FIREBALL.get()))
                .save(provider, MITMUtil.id("wand/large_fireball"));

        getShaped(MITMWandAbilities.SMALL_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MITMItems.HANDLE.get())
                .define('C', getWandCore(MITMWandAbilities.SMALL_FIREBALL.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', Items.FIREWORK_STAR)
                .group("wands")
                .unlockedBy("has_core", hasCore(MITMWandAbilities.SMALL_FIREBALL.get()))
                .save(provider, MITMUtil.id("wand/small_fireball"));

        getShaped(MITMWandAbilities.HEAL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MITMItems.HANDLE.get())
                .define('C', getWandCore(MITMWandAbilities.HEAL.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING))
                .group("wands")
                .unlockedBy("has_core", hasCore(MITMWandAbilities.HEAL.get()))
                .save(provider, MITMUtil.id("wand/heal"));

        getShaped(MITMWandAbilities.LIGHTNING.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', MITMItems.HANDLE.get())
                .define('C', getWandCore(MITMWandAbilities.LIGHTNING.get()))
                .define('L', Tags.Items.LEATHERS)
                .define('B', MITMItems.CLOUD_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(MITMWandAbilities.LIGHTNING.get()))
                .save(provider, MITMUtil.id("wand/lightning"));
    }

    public static ShapedWandRecipeBuilder getShaped(WandAbility ability) {
        return new ShapedWandRecipeBuilder(MITMUtil.CUSTOM_WANDS.get(ability.getKey()));
    }

    public static ShapelessWandRecipeBuilder getShapeless(WandAbility ability) {
        return new ShapelessWandRecipeBuilder(MITMUtil.CUSTOM_WANDS.get(ability.getKey()));
    }

    public static ShapelessWandRecipeBuilder getShapeless(ItemStack wand) {
        return new ShapelessWandRecipeBuilder(wand);
    }

    public static ItemStack getWandCore(WandAbility ability) {
        return MITMUtil.CUSTOM_WAND_CORES.get(ability.getKey());
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
        return inventoryTrigger(Arrays.stream(items).map(ItemPredicate.Builder::build).<ItemPredicate>toArray(p_297943_ -> new ItemPredicate[p_297943_]));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
        return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
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
        return has(MITMUtil.CUSTOM_WAND_CORES.get(ability.getKey()));
    }

    private static void addWandUpgradeRecipes(RecipeOutput output) {
        for (var wand : MITMUtil.CUSTOM_WANDS.values()) {
            var result = wand.copy();
            if (!result.has(MITMDataComponents.WAND)) {
                return;
            }
            WandComponent component = result.get(MITMDataComponents.WAND);
            component = component.withPoweredUp(true);
            WandAbilityInstance primary = component.primary();

            getShapeless(result)
                    .requires(MITMItems.CTHULHU_EYE.get())
                    .requires(wand)
                    .unlockedBy("has_cthulhu_eye", has(MITMItems.CTHULHU_EYE.get()))
                    .group("wand_upgrades")
                    .save(output, MITMUtil.id("wand_upgrade/" + primary.getId().getPath()));
        }
    }
}
