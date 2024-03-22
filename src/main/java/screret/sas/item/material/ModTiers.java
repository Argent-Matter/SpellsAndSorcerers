package screret.sas.item.material;

import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.common.Tags;
import screret.sas.ModTags;

public class ModTiers {
    public static final SimpleTier SOULSTEEL = new SimpleTier(5, 3417, 10.0F, 5.0F, 18, Tags.Blocks.NEEDS_NETHERITE_TOOL, () -> Ingredient.of(ModTags.Items.SOULSTEEL_INGOTS));
}
