package dev.screret.motm.core.ext;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public interface IJigsawStructureExtension {

    default Holder<StructureTemplatePool> motm$getStartPool() {
        throw new AssertionError("Mixin didn't apply");
    }

    default Optional<ResourceLocation> motm$getStartJigsawName() {
        throw new AssertionError("Mixin didn't apply");
    }

    default int motm$getMaxDepth() {
        throw new AssertionError("Mixin didn't apply");
    }
}
