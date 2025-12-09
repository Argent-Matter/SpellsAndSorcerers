package dev.screret.motm.core.ext;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;
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

    default Optional<Heightmap.Types> motm$getProjectStartToHeightmap() {
        throw new AssertionError("Mixin didn't apply");
    }

    default List<PoolAliasBinding> motm$getPoolAliases() {
        throw new AssertionError("Mixin didn't apply");
    }

    default DimensionPadding motm$getDimensionPadding() {
        throw new AssertionError("Mixin didn't apply");
    }

    default LiquidSettings motm$getLiquidSettings() {
        throw new AssertionError("Mixin didn't apply");
    }
}
