package screret.sas.data.recipe.provider;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.Tags;
import screret.sas.Util;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.data.recipe.builder.ShapedWandRecipeBuilder;
import screret.sas.data.recipe.builder.ShapelessWandRecipeBuilder;
import screret.sas.item.ModItems;
import java.util.function.Consumer;

public class WandRecipeProvider extends RecipeProvider {

    public WandRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        Util.addItems();

        addWandUpgradeRecipes(consumer);

        getShaped(ModWandAbilities.DAMAGE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.DAMAGE.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', ModItems.SOUL_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.DAMAGE.get()))
                .save(consumer, Util.id("wand/damage"));

        getShaped(ModWandAbilities.EXPLODE.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.EXPLODE.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.TNT)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.EXPLODE.get()))
                .save(consumer, Util.id("wand/explosion"));

        getShaped(ModWandAbilities.LARGE_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.LARGE_FIREBALL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.FIRE_CHARGE)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.LARGE_FIREBALL.get()))
                .save(consumer, Util.id("wand/large_fireball"));

        getShaped(ModWandAbilities.SMALL_FIREBALL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.SMALL_FIREBALL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', Items.FIREWORK_STAR)
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.SMALL_FIREBALL.get()))
                .save(consumer, Util.id("wand/small_fireball"));

        getShaped(ModWandAbilities.HEAL.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.HEAL.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HEALING))
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.HEAL.get()))
                .save(consumer, Util.id("wand/heal"));

        getShaped(ModWandAbilities.LIGHTNING.get())
                .pattern("BCB")
                .pattern("LSL")
                .define('S', ModItems.HANDLE.get())
                .define('C', getWandCore(ModWandAbilities.LIGHTNING.get()))
                .define('L', Tags.Items.LEATHER)
                .define('B', ModItems.CLOUD_BOTTLE.get())
                .group("wands")
                .unlockedBy("has_core", hasCore(ModWandAbilities.LIGHTNING.get()))
                .save(consumer, Util.id("wand/lightning"));

    }

    public ShapedWandRecipeBuilder getShaped(WandAbility ability){
        return new ShapedWandRecipeBuilder(Util.customWands.get(ability.getKey()));
    }

    public ShapelessWandRecipeBuilder getShapeless(WandAbility ability){
        return new ShapelessWandRecipeBuilder(Util.customWands.get(ability.getKey()));
    }

    public ShapelessWandRecipeBuilder getShapeless(ItemStack wand){
        return new ShapelessWandRecipeBuilder(wand);
    }

    public ItemStack getWandCore(WandAbility ability){
        return Util.customWandCores.get(ability.getKey());
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemStack stack) {
        var saved = stack.serializeNBT();
        var tag = new CompoundTag();
        if(saved.contains("tag")){
            tag = tag.merge(saved.getCompound("tag"));
        }
        if(saved.contains("ForgeCaps")){
            tag.put("ForgeCaps", saved.getCompound("ForgeCaps"));
        }
        if(saved.contains("tag") || saved.contains("ForgeCaps")) return inventoryTrigger(ItemPredicate.Builder.item().of(stack.getItem()).hasNbt(tag).build());
        else return inventoryTrigger(ItemPredicate.Builder.item().of(stack.getItem()).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance hasCore(WandAbility ability) {
        return has(Util.customWandCores.get(ability.getKey()));
    }

    private void addWandUpgradeRecipes(Consumer<FinishedRecipe> consumer){
        for (var wand : Util.customWands.values()){
            var result = wand.copy();
            WandAbilityInstance mainAbility = null;
            if(result.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()){
                var cap = result.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
                cap.setPoweredUp(true);
                mainAbility = cap.getAbility();
            }
            getShapeless(result)
                    .requires(ModItems.CTHULHU_EYE.get())
                    .requires(wand)
                    .unlockedBy("has_cthulu_eye", has(ModItems.CTHULHU_EYE.get()))
                    .group("wand_upgrades")
                    .save(consumer, Util.id("wand_upgrade/" + mainAbility.getId().getPath()));
        }
    }
}
