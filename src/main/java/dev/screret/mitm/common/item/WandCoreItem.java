package dev.screret.mitm.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import dev.screret.mitm.data.MITMDataComponents;

public class WandCoreItem extends Item {
    public static final String ABILITY_KEY = "ability";

    public WandCoreItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = "ability.mitm.dummy";
        if (stack.has(MITMDataComponents.WAND_CORE)) {
            name = stack.get(MITMDataComponents.WAND_CORE).getId().toLanguageKey(ABILITY_KEY);
        }
        return Component.translatable(super.getDescriptionId(stack), Component.translatable(name));
    }
}
