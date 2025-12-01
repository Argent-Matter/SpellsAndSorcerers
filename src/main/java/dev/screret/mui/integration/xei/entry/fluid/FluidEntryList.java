package dev.screret.mui.integration.xei.entry.fluid;

import net.neoforged.neoforge.fluids.FluidStack;

import dev.screret.mui.integration.xei.entry.EntryList;

import java.util.List;

public sealed interface FluidEntryList extends EntryList<FluidStack>
                                       permits FluidStackList, FluidTagList, FluidHolderSetList {

    List<FluidStack> getStacks();

    boolean isEmpty();
}
