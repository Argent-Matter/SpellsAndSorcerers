package dev.screret.motm.data;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class MOTMTiers {

    public static final SimpleTier SOULSTEEL = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 3417,
            10.0F, 5.0F, 18, () -> Ingredient.of(MOTMTags.Items.SOULSTEEL_INGOTS));
}
