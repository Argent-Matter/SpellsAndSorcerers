package screret.sas.integration.rei.wand;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class WandCoreSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final WandCoreSubtypeInterpreter INSTANCE = new WandCoreSubtypeInterpreter();

    private WandCoreSubtypeInterpreter() {

    }

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
        if (!itemStack.hasTag()) {
            return IIngredientSubtypeInterpreter.NONE;
        }
        return Component.translatable(itemStack.getTag().getString("ability")).getString();
    }
}
