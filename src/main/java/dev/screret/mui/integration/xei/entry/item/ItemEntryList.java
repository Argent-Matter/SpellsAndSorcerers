package dev.screret.mui.integration.xei.entry.item;

import net.minecraft.world.item.ItemStack;

import dev.screret.mui.integration.xei.entry.EntryList;

import java.util.List;

public sealed interface ItemEntryList extends EntryList<ItemStack>
                                      permits ItemStackList, ItemTagList, ItemHolderSetList {

    List<ItemStack> getStacks();

    boolean isEmpty();
}
