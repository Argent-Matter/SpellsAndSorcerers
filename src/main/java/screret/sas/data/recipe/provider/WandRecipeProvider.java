package screret.sas.data.recipe.provider;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import screret.sas.Util;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.data.recipe.builder.ShapedWandRecipeBuilder;
import screret.sas.data.recipe.builder.ShapelessWandRecipeBuilder;
import screret.sas.item.ModItems;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WandRecipeProvider {
    protected static void buildRecipes(RecipeOutput provider) {
        Util.generateWandItems();
        addWandUpgradeRecipes(provider);

        getShaped(ModWandAbilities.DAMAGE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.DAMAGE.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', ModItems.SOUL_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.DAMAGE.get()))
                .save(provider, Util.id("wand/damage"));

        getShaped(ModWandAbilities.EXPLODE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.EXPLODE.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.TNT)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.EXPLODE.get()))
                .save(provider, Util.id("wand/explosion"));

        getShaped(ModWandAbilities.LARGE_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.LARGE_FIREBALL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.FIRE_CHARGE)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.LARGE_FIREBALL.get()))
                .save(provider, Util.id("wand/large_fireball"));

        getShaped(ModWandAbilities.SMALL_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.SMALL_FIREBALL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.FIREWORK_STAR)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.SMALL_FIREBALL.get()))
                .save(provider, Util.id("wand/small_fireball"));

        getShaped(ModWandAbilities.HEAL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.HEAL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING))
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.HEAL.get()))
                .save(provider, Util.id("wand/heal"));

        getShaped(ModWandAbilities.LIGHTNING.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.LIGHTNING.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', ModItems.CLOUD_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.LIGHTNING.get()))
                .save(provider, Util.id("wand/lightning"));
    }

    public static ShapedWandRecipeBuilder getShaped(WandAbility ability) {
        return new ShapedWandRecipeBuilder(Util.CUSTOM_WANDS.get(ability.getKey()));
    }

    public static ShapelessWandRecipeBuilder getShapeless(WandAbility ability) {
        return new ShapelessWandRecipeBuilder(Util.CUSTOM_WANDS.get(ability.getKey()));
    }

    public static ShapelessWandRecipeBuilder getShapeless(ItemStack wand) {
        return new ShapelessWandRecipeBuilder(wand);
    }

    public static ItemStack getWandCore(WandAbility ability) {
        return Util.CUSTOM_WAND_CORES.get(ability.getKey());
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... pItems) {
        return inventoryTrigger(Arrays.stream(pItems).map(ItemPredicate.Builder::build).<ItemPredicate>toArray(p_297943_ -> new ItemPredicate[p_297943_]));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... pPredicates) {
        return CriteriaTriggers.INVENTORY_CHANGED
                .createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(pPredicates)));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike pItemLike) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(pItemLike));
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemStack stack) {
        var saved = stack.save(new CompoundTag());
        var tag = new CompoundTag();
        if (saved.contains("tag")) {
            tag = tag.merge(saved.getCompound("tag"));
        }
        if (saved.contains("ForgeCaps")) {
            tag.put("ForgeCaps", saved.getCompound("ForgeCaps"));
        }
        if (saved.contains("tag") || saved.contains("ForgeCaps"))
            return inventoryTrigger(ItemPredicate.Builder.item().of(stack.getItem()).hasNbt(tag).build());
        else
            return inventoryTrigger(ItemPredicate.Builder.item().of(stack.getItem()).build());
    }

    protected static Criterion<InventoryChangeTrigger.TriggerInstance> hasCore(WandAbility ability) {
        return has(Util.CUSTOM_WAND_CORES.get(ability.getKey()));
    }

    private static void addWandUpgradeRecipes(RecipeOutput output) {
        for (var wand : Util.CUSTOM_WANDS.values()) {
            var result = wand.copy();
            WandAbilityInstance mainAbility = null;
            if (result.getCapability(ICapabilityWandAbility.WAND_ABILITY) != null) {
                var cap = result.getCapability(ICapabilityWandAbility.WAND_ABILITY);
                cap.setPoweredUp(true);
                mainAbility = cap.getMainAbility();
            }
            if (mainAbility == null) {
                return;
            }
            getShapeless(result)
                    .requires(ModItems.CTHULHU_EYE.get())
                    .requires(wand)
                    .unlockedBy("has_cthulhu_eye", has(ModItems.CTHULHU_EYE.get()))
                    .group("wand_upgrades")
                    .save(output, Util.id("wand_upgrade/" + mainAbility.getId().getPath()));
        }
    }
}
