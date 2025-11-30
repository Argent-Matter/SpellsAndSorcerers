package dev.screret.mitm.common.item;

import dev.screret.mitm.api.ability.WandAbilityInstance;
import dev.screret.mitm.data.MITMDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import org.jetbrains.annotations.NotNull;

public class WandCoreItem extends Item {

    public static final String ABILITY_KEY = "ability";

    public WandCoreItem() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        String name = "ability.mitm.dummy";
        WandAbilityInstance component = stack.get(MITMDataComponents.WAND_CORE);
        if (component != null) {
            name = component.getId().toLanguageKey(ABILITY_KEY);
        }
        return Component.translatable(super.getDescriptionId(stack), Component.translatable(name));
    }
}
