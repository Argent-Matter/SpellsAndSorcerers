package dev.screret.mitm.common.data.provider.blockstate;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.data.MITMBlocks;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MITMBlockStateProvider extends BlockStateProvider {

    public MITMBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MagicOfTheMind.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        createSummoningCircle();
    }

    private void createSummoningCircle() {
        ModelFile.UncheckedModelFile summoningCircleModel = new ModelFile.UncheckedModelFile(MITMUtil.id("block/summoning_circle"));

        getVariantBuilder(MITMBlocks.SUMMONING_CIRCLE.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(summoningCircleModel)
                        .build());
    }
}
