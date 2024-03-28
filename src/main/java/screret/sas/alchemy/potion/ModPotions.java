package screret.sas.alchemy.potion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.alchemy.effect.ModMobEffects;
import screret.sas.item.ModItems;

import java.util.function.Supplier;

public class ModPotions {

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, SpellsAndSorcerers.MODID);

    public static final Supplier<Potion> MANA = POTIONS.register("mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 3600)));
    public static final Supplier<Potion> LONG_MANA = POTIONS.register("long_mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 9600)));
    public static final Supplier<Potion> STRONG_MANA = POTIONS.register("strong_mana", () -> new Potion(new MobEffectInstance(ModMobEffects.MANA.get(), 1800, 1)));

    public static void registerPotionMixes() {
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(ModItems.SOUL_BOTTLE.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA.get())
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA.get())),
                Ingredient.of(Items.REDSTONE),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LONG_MANA.get())
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA.get())),
                Ingredient.of(Items.GLOWSTONE_DUST),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STRONG_MANA.get())
        );
    }
}

