package dev.screret.motm.core.mixin.vanilla;

import dev.screret.motm.core.ext.IJigsawStructureExtension;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin implements IJigsawStructureExtension {

    @Shadow
    @Final
    private Holder<StructureTemplatePool> startPool;
    @Shadow
    @Final
    private Optional<ResourceLocation> startJigsawName;
    @Shadow
    @Final
    private int maxDepth;
    @Shadow
    @Final
    private Optional<Heightmap.Types> projectStartToHeightmap;
    @Shadow
    @Final
    private List<PoolAliasBinding> poolAliases;
    @Shadow
    @Final
    private DimensionPadding dimensionPadding;
    @Shadow
    @Final
    private LiquidSettings liquidSettings;

    @Override
    public Holder<StructureTemplatePool> motm$getStartPool() {
        return this.startPool;
    }

    @Override
    public Optional<ResourceLocation> motm$getStartJigsawName() {
        return this.startJigsawName;
    }

    @Override
    public int motm$getMaxDepth() {
        return this.maxDepth;
    }

    @Override
    public Optional<Heightmap.Types> motm$getProjectStartToHeightmap() {
        return this.projectStartToHeightmap;
    }

    @Override
    public List<PoolAliasBinding> motm$getPoolAliases() {
        return this.poolAliases;
    }

    @Override
    public DimensionPadding motm$getDimensionPadding() {
        return this.dimensionPadding;
    }

    @Override
    public LiquidSettings motm$getLiquidSettings() {
        return this.liquidSettings;
    }
}
