package dev.screret.motm.api.memory;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
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
import java.util.Optional;

/**
 * This is currently just a stub class until I implement the whole system for animated NPCs in memories
 */
public class MemoryScene {

    // spotless:off
    private static final MapCodec<JigsawStructure> JIGSAW_CODEC = RecordCodecBuilder.<JigsawStructure>mapCodec(instance -> instance.group(
            Structure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(JigsawStructure::getStartPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 20).fieldOf("size").forGetter(structure -> structure.maxDepth),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    Codec.list(PoolAliasBinding.CODEC).optionalFieldOf("pool_aliases", List.of()).forGetter(structure -> structure.poolAliases),
                    DimensionPadding.CODEC.optionalFieldOf("dimension_padding", JigsawStructure.DEFAULT_DIMENSION_PADDING).forGetter(structure -> structure.dimensionPadding),
                    LiquidSettings.CODEC.optionalFieldOf("liquid_settings", JigsawStructure.DEFAULT_LIQUID_SETTINGS).forGetter(structure -> structure.liquidSettings)
            )
            .apply(instance, JigsawStructure::new));
    // spotless:on

    @Getter
    private final JigsawStructure structure;

    public MemoryScene(JigsawStructure structure) {
        this.structure = structure;
    }

    public static JigsawStructure makeMemoryStructure(Structure.StructureSettings settings,
                                                      Holder<StructureTemplatePool> startPool,
                                                      Optional<ResourceLocation> startJigsawName,
                                                      int maxDepth,
                                                      HeightProvider startHeight,
                                                      boolean useExpansionHack,
                                                      Optional<Heightmap.Types> projectStartToHeightmap,
                                                      int maxDistanceFromCenter,
                                                      List<PoolAliasBinding> poolAliases,
                                                      DimensionPadding dimensionPadding,
                                                      LiquidSettings liquidSettings
    ) {
        return new JigsawStructure()
    }
}
