package dev.screret.sas.datagen.blockstate;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import dev.screret.sas.SpellsAndSorcerers;
import dev.screret.sas.Util;
import dev.screret.sas.data.ModBlocks;

public class ModBlockstateProvider extends BlockStateProvider {
    public ModBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SpellsAndSorcerers.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile summonSign = models().singleTexture("summon_sign", ResourceLocation.fromNamespaceAndPath("geckolib3", "block/box"), Util.id("block/summon_sign"));

        getVariantBuilder(ModBlocks.SUMMON_SIGN.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(summonSign)
                        .build()
                );
    }
}
