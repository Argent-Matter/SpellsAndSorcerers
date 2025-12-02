package dev.screret.modularui.integration.xei.entry.fluid;

import dev.screret.modularui.integration.xei.entry.EntryList;

import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public sealed interface FluidEntryList extends EntryList<FluidStack>
                                       permits FluidStackList, FluidTagList, FluidHolderSetList {

    List<FluidStack> getStacks();

    boolean isEmpty();
}
