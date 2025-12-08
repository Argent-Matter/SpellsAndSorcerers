package dev.screret.motm.common.data.provider.model;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.data.MOTMBlocks;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
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
        simpleBlockWithItem(MOTMBlocks.PALANTIR.get(), getUncheckedFile(MOTMUtil.id("palantir")));
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
}
