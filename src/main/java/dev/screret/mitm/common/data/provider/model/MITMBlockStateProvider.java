package dev.screret.mitm.common.data.provider.model;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.common.block.PotionDistilleryBlock;
import dev.screret.mitm.data.MITMBlocks;

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

public class MITMBlockStateProvider extends BlockStateProvider {

    public MITMBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MagicOfTheMind.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(MITMBlocks.GLINT_ORE.get());
        simpleBlockWithItem(MITMBlocks.SOULSTEEL_BLOCK.get());
        simpleBlockWithItem(MITMBlocks.WAND_TABLE.get(), models().getExistingFile(MITMUtil.id("wand_table")));
        simpleBlockWithItem(MITMBlocks.PALANTIR.get(), getUncheckedFile(MITMUtil.id("palantir")));

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
        ModelFile.UncheckedModelFile summoningCircleModel = getUncheckedFile(MITMUtil.id("summoning_circle"));

        getVariantBuilder(MITMBlocks.SUMMONING_CIRCLE.get())
                .forAllStates(state -> ConfiguredModel.builder()
                        .modelFile(summoningCircleModel)
                        .build());
    }

    private void createPotionDistillery() {
        Block block = MITMBlocks.POTION_DISTILLERY.get();

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
