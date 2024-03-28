package screret.sas.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import screret.sas.ModTags;
import screret.sas.SpellsAndSorcerers;

import java.util.concurrent.CompletableFuture;

public class SASBiomeTagsProvider extends BiomeTagsProvider {
    public SASBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SpellsAndSorcerers.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Biomes.HAS_RITUAL_SPOT).add(Biomes.PLAINS).add(Biomes.FROZEN_PEAKS).add(Biomes.MEADOW).add(Biomes.BIRCH_FOREST);
        tag(ModTags.Biomes.HAS_WIZARD_TOWER).add(Biomes.DARK_FOREST).add(Biomes.SPARSE_JUNGLE).add(Biomes.MEADOW).add(Biomes.ICE_SPIKES);
    }
}
