package dev.screret.motm.common.data.provider.model;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.block.PortStoneBlock;
import dev.screret.motm.data.block.MOTMBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import org.jetbrains.annotations.Nullable;

public class MOTMBlockStateProvider extends BlockStateProvider {

    public MOTMBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MagicOfTheMind.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(MOTMBlocks.GLINT_ORE.get());
        simpleBlockWithItem(MOTMBlocks.SOULSTEEL_BLOCK.get());
        randomRotatedMirroredBlockWithItem(MOTMBlocks.MEMORYSTONE.get());
        randomRotatedMirroredBlockWithItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE.get());

        getVariantBuilder(MOTMBlocks.PORT_STONE.get())
                .forAllStatesExcept(state -> {
                    PortStoneBlock.Part part = state.getValue(PortStoneBlock.PART);
                    return ConfiguredModel.builder()
                            .modelFile(getExistingFile(MOTMUtil.id("port_stone_" + part.getSerializedName())))
                            .build();
                }, PortStoneBlock.TRIGGERED, PortStoneBlock.WATERLOGGED);
    }

    public void simpleBlockWithItem(Block block) {
        simpleBlockWithItem(block, cubeAll(block));
    }

    public void randomRotatedBlock(Block block) {
        randomRotatedBlock(block, cubeAll(block), null);
    }

    public void randomRotatedBlock(Block block, ModelFile model, @Nullable ModelFile mirroredModel) {
        var builder = getVariantBuilder(block);
        var partialState = builder.partialState();

        builder.addModels(partialState, makeRotationModels(model, false, true));
        if (mirroredModel != null) {
            builder.addModels(partialState, makeRotationModels(mirroredModel, false, true));
        }
    }

    public void randomRotatedBlockWithItem(Block block) {
        ModelFile model = cubeAll(block);
        randomRotatedBlockWithItem(block, model, null);
    }

    public void randomRotatedMirroredBlockWithItem(Block block) {
        ModelFile model = cubeAll(block);
        ModelFile mirroredModel = cubeMirroredAll(block);
        randomRotatedBlockWithItem(block, model, mirroredModel);
    }

    public void randomRotatedBlockWithItem(Block block, ModelFile model, @Nullable ModelFile mirrored) {
        randomRotatedBlock(block, model, mirrored);
        simpleBlockItem(block, model);
    }

    protected ConfiguredModel[] makeRotationModels(ModelFile model, boolean rotateX, boolean rotateY) {
        ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
        if (rotateX && rotateY) {
            for (int x = 0; x <= 270; x += 90) {
                for (int y = 0; y <= 270; y += 90) {
                    if (x == 0 && y == 0) {
                        // skip x = 0 when y = 0
                        continue;
                    }
                    builder = builder.nextModel().modelFile(model).rotationX(x).rotationY(y);
                }
            }
        } else if (rotateX) {
            // skip x = 0
            for (int x = 90; x <= 270; x += 90) {
                builder = builder.nextModel().modelFile(model).rotationX(x);
            }
        } else if (rotateY) {
            // skip y = 0
            for (int y = 90; y <= 270; y += 90) {
                builder = builder.nextModel().modelFile(model).rotationY(y);
            }
        }
        return builder.build();
    }

    public ModelFile cubeMirroredAll(Block block) {
        return cubeMirroredAll(name(block) + "_mirrored", blockTexture(block));
    }

    public BlockModelBuilder cubeMirroredAll(String name, ResourceLocation texture) {
        return models().singleTexture(name, mcLoc(ModelProvider.BLOCK_FOLDER + "/cube_mirrored_all"), "all", texture);
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

    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return key(block).getPath();
    }
}
