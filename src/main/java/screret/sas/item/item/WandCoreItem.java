package screret.sas.item.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class WandCoreItem extends Item {
    public static final String ABILITY_KEY = "ability";

    public WandCoreItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = "item.sas.basic";
        if (stack.hasTag() && stack.getTag().contains(ABILITY_KEY)) {
            name = new ResourceLocation(stack.getTag().getString(ABILITY_KEY)).toLanguageKey(ABILITY_KEY);
        }
        return Component.translatable(super.getDescriptionId(stack), Component.translatable(name));
    }
}
