package screret.sas.data.blockstate;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.block.ModBlocks;

public class ModBlockstateProvider extends BlockStateProvider {
    public ModBlockstateProvider(PackOutput packOutput, ExistingFileHelper exFileHelper) {
        super(packOutput, SpellsAndSorcerers.MODID, exFileHelper);
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
