package screret.sas.data.blockstate;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.block.ModBlocks;

public class ModBlockstateProvider extends BlockStateProvider {
    public ModBlockstateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SpellsAndSorcerers.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile summon_sign = models().singleTexture("summon_sign", new ResourceLocation("geckolib3:block/box"), Util.id("block/summon_sign"));

        getVariantBuilder(ModBlocks.SUMMON_SIGN.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(summon_sign)
                        .build()
                );
    }
}
