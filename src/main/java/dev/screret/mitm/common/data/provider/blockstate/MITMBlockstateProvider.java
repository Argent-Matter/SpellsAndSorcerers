package dev.screret.mitm.common.data.provider.blockstate;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.data.MITMBlocks;

public class MITMBlockstateProvider extends BlockStateProvider {
    public MITMBlockstateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MagicOfTheMind.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile summonSign = models().singleTexture("summon_sign", ResourceLocation.fromNamespaceAndPath("geckolib3", "block/box"), MITMUtil.id("block/summon_sign"));

        getVariantBuilder(MITMBlocks.SUMMON_SIGN.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(summonSign)
                        .build()
                );
    }
}
