package dev.screret.motm.common.data.provider.model;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.block.PotionDistilleryBlock;
import dev.screret.motm.data.MOTMBlocks;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MOTMBlockStateProvider extends BlockStateProvider {

    public MOTMBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MagicOfTheMind.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(MOTMBlocks.GLINT_ORE.get());
        simpleBlockWithItem(MOTMBlocks.SOULSTEEL_BLOCK.get());
        simpleBlockWithItem(MOTMBlocks.WAND_TABLE.get(), models().getExistingFile(MOTMUtil.id("wand_table")));
        simpleBlockWithItem(MOTMBlocks.PALANTIR.get(), getUncheckedFile(MOTMUtil.id("palantir")));

        createSummoningCircle();
        createPotionDistillery();
    }

    public void simpleBlockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }

    public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
        return models().getExistingFile(path);
    }

    public ModelFile.UncheckedModelFile getUncheckedFile(ResourceLocation path) {
        return new ModelFile.UncheckedModelFile(extendWithFolder(path, ModelProvider.BLOCK_FOLDER));
    }

    private ResourceLocation extendWithFolder(ResourceLocation rl, String folder) {
        if (rl.getPath().contains("/")) {
            return rl;
        }
        return rl.withPrefix(folder + "/");
    }

    private void createSummoningCircle() {
        ModelFile.UncheckedModelFile summoningCircleModel = getUncheckedFile(MOTMUtil.id("summoning_circle"));

        getVariantBuilder(MOTMBlocks.SUMMONING_CIRCLE.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(summoningCircleModel)
                        .build());
    }

    private void createPotionDistillery() {
        Block block = MOTMBlocks.POTION_DISTILLERY.get();

        var offModel = models().withExistingParent("potion_distillery", "orientable_with_bottom")
                .texture("front", ModelLocationUtils.getModelLocation(block, "_front"))
                .texture("side", ModelLocationUtils.getModelLocation(block, "_side"))
                .texture("top", ModelLocationUtils.getModelLocation(block, "_top"))
                .texture("bottom", ModelLocationUtils.getModelLocation(block, "_bottom"));
        var onModel = models().getBuilder("potion_distillery_on").parent(offModel)
                .texture("side", ModelLocationUtils.getModelLocation(block, "_side_on"))
                .texture("top", ModelLocationUtils.getModelLocation(block, "_top_on"));

        simpleBlockItem(block, offModel);

        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction facing = state.getValue(PotionDistilleryBlock.FACING);
                    boolean active = state.getValue(PotionDistilleryBlock.ACTIVE);

                    return ConfiguredModel.builder()
                            .modelFile(active ? onModel : offModel)
                            .rotationY((int) facing.toYRot())
                            .build();
                });
    }
}
