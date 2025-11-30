package dev.screret.motm.common.data.provider.tag;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.data.MOTMTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class MOTMBiomeTagsProvider extends BiomeTagsProvider {

    public MOTMBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                 ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MagicOfTheMind.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(MOTMTags.Biomes.HAS_RITUAL_SPOT).add(Biomes.PLAINS).add(Biomes.FROZEN_PEAKS).add(Biomes.MEADOW)
                .add(Biomes.BIRCH_FOREST);
        tag(MOTMTags.Biomes.HAS_WIZARD_TOWER).add(Biomes.DARK_FOREST).add(Biomes.SPARSE_JUNGLE).add(Biomes.MEADOW)
                .add(Biomes.ICE_SPIKES);
    }
}
