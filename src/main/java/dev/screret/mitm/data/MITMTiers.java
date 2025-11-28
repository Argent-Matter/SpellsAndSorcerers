package dev.screret.mitm.data;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class MITMTiers {

    public static final SimpleTier SOULSTEEL = new SimpleTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 3417,
            10.0F, 5.0F, 18, () -> Ingredient.of(MITMTags.Items.SOULSTEEL_INGOTS));
}
