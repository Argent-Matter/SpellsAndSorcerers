package dev.screret.mitm.common.menu.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import dev.screret.mitm.common.block.entity.PotionDistilleryBlockEntity;
import dev.screret.mitm.common.menu.container.PotionDistilleryMenu;

public class DistilleryFuelSlot extends SlotItemHandler {
    private final PotionDistilleryMenu menu;

    public DistilleryFuelSlot(PotionDistilleryMenu menu, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.menu = menu;
    }

    public boolean mayPlace(ItemStack stack) {
        return PotionDistilleryBlockEntity.isFuel(stack) || isBucket(stack);
    }

    public int getMaxStackSize(ItemStack stack) {
        return isBucket(stack) ? 1 : super.getMaxStackSize(stack);
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.is(Items.BUCKET);
    }
}
