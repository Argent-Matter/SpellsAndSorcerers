package dev.screret.motm.api.memory;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This is currently just a stub class until I implement the whole system for animated NPCs in memories
 */
public class MemoryScene {

    // spotless:off
    private static final int MAX_MEMORY_STRUCTURE_RADIUS = 256;
    private static final Structure.StructureSettings DEFAULT_STRUCTURE_SETTINGS = new Structure.StructureSettings(HolderSet.direct(), Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE);

    private static final MapCodec<JigsawStructure> JIGSAW_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(JigsawStructure::motm$getStartPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(JigsawStructure::motm$getStartJigsawName),
            Codec.intRange(0, 20).fieldOf("size").forGetter(JigsawStructure::motm$getMaxDepth),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(JigsawStructure::motm$getProjectStartToHeightmap),
            Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(JigsawStructure::motm$getPoolAliases),
            DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(JigsawStructure::motm$getDimensionPadding),
            LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(JigsawStructure::motm$getLiquidSettings)
    ).apply(instance, MemoryScene::makeMemoryStructure));

    public static JigsawStructure makeMemoryStructure(Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
                                                      int maxDepth, Optional<Heightmap.Types> projectStartToHeightmap,
                                                      List<PoolAliasBinding> poolAliases, DimensionPadding dimensionPadding, LiquidSettings liquidSettings) {
        return new JigsawStructure(DEFAULT_STRUCTURE_SETTINGS, startPool, startJigsawName, maxDepth, ConstantHeight.ZERO, true,
                projectStartToHeightmap, MAX_MEMORY_STRUCTURE_RADIUS, poolAliases, dimensionPadding, liquidSettings);
    }

    public static final Codec<MemoryScene> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            JIGSAW_CODEC.fieldOf("structure").forGetter(MemoryScene::getStructure)
    ).apply(instance, MemoryScene::new));
    // spotless:on

    @Getter
    private final JigsawStructure structure;

    public MemoryScene(JigsawStructure structure) {
        this.structure = structure;
    }
}
