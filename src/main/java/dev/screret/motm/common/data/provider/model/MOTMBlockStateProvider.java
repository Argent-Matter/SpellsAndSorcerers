package dev.screret.motm.common.data.provider.model;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.block.PortStoneBlock;
import dev.screret.motm.data.block.MOTMBlocks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public class MOTMBlockStateProvider extends BlockStateProvider {

    public MOTMBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MagicOfTheMind.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(MOTMBlocks.GLINT_ORE);
        simpleBlockWithItem(MOTMBlocks.SOULSTEEL_BLOCK);

        getVariantBuilder(MOTMBlocks.PORT_STONE.get())
                .forAllStatesExcept(state -> {
                    PortStoneBlock.Part part = state.getValue(PortStoneBlock.PART);
                    return ConfiguredModel.builder()
                            .modelFile(getExistingFile(MOTMUtil.id("port_stone_" + part.getSerializedName())))
                            .build();
                }, PortStoneBlock.TRIGGERED, PortStoneBlock.WATERLOGGED);

        // region memorystone block set
        randomRotatedMirroredBlockWithItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE);
        slabBlockWithItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE_SLAB, MOTMBlocks.UNAWAKENED_MEMORYSTONE);
        stairsBlockWithItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE_STAIRS, MOTMBlocks.UNAWAKENED_MEMORYSTONE);
        wallBlockWithItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE_WALL, MOTMBlocks.UNAWAKENED_MEMORYSTONE);

        randomRotatedMirroredBlockWithItem(MOTMBlocks.MEMORYSTONE);
        stairsBlockWithItem(MOTMBlocks.MEMORYSTONE_STAIRS, MOTMBlocks.MEMORYSTONE);
        slabBlockWithItem(MOTMBlocks.MEMORYSTONE_SLAB, MOTMBlocks.MEMORYSTONE);
        wallBlockWithItem(MOTMBlocks.MEMORYSTONE_WALL, MOTMBlocks.MEMORYSTONE);

        simpleBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE);
        stairsBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_STAIRS, MOTMBlocks.POLISHED_MEMORYSTONE);
        slabBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_SLAB, MOTMBlocks.POLISHED_MEMORYSTONE);
        wallBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_WALL, MOTMBlocks.MEMORYSTONE);
        pressurePlateBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_PRESSURE_PLATE, MOTMBlocks.POLISHED_MEMORYSTONE);
        buttonBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_BUTTON, MOTMBlocks.POLISHED_MEMORYSTONE);
        simpleBlockWithItem(MOTMBlocks.CHISELED_POLISHED_MEMORYSTONE);

        simpleBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS);
        simpleBlockWithItem(MOTMBlocks.CRACKED_POLISHED_MEMORYSTONE_BRICKS);
        stairsBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_STAIRS, MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS);
        slabBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_SLAB, MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS);
        wallBlockWithItem(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_WALL, MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS);
        // endregion
    }

    public void simpleBlockWithItem(Supplier<? extends Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

    public void simpleBlockWithItem(Supplier<? extends Block> block, ResourceLocation texture) {
        simpleBlockWithItem(block.get(), models().cubeAll(name(block), texture));
    }

    public void stairsBlockWithItem(Supplier<? extends StairBlock> block, Supplier<? extends Block> baseBlock) {
        ResourceLocation texture = blockTexture(baseBlock);
        String baseName = key(block.get()).toString();
        ModelFile stairs = models().stairs(baseName, texture, texture, texture);
        ModelFile stairsInner = models().stairsInner(baseName + "_inner", texture, texture, texture);
        ModelFile stairsOuter = models().stairsOuter(baseName + "_outer", texture, texture, texture);

        stairsBlock(block.get(), stairs, stairsInner, stairsOuter);
        simpleBlockItem(block.get(), stairs);
    }

    public void wallBlockWithItem(Supplier<? extends WallBlock> block, Supplier<? extends Block> baseBlock) {
        ResourceLocation texture = blockTexture(baseBlock);
        wallBlock(block.get(), texture);
        itemModels().wallInventory(name(block.get()), texture);
    }

    public void slabBlockWithItem(Supplier<? extends SlabBlock> block, Supplier<? extends Block> baseBlock) {
        ResourceLocation texture = blockTexture(baseBlock);
        ModelFile bottom = models().slab(name(block), texture, texture, texture);
        ModelFile top = models().slabTop(name(block) + "_top", texture, texture, texture);
        ModelFile doubleSlab = getExistingFile(key(baseBlock.get()));

        slabBlock(block.get(), top, bottom, doubleSlab);
        simpleBlockItem(block.get(), bottom);
    }

    public void pressurePlateBlockWithItem(Supplier<? extends PressurePlateBlock> block, Supplier<? extends Block> baseBlock) {
        ResourceLocation texture = blockTexture(baseBlock);
        ModelFile pressurePlate = models().pressurePlate(name(block), texture);
        ModelFile pressurePlateDown = models().pressurePlateDown(name(block) + "_down", texture);

        pressurePlateBlock(block.get(), pressurePlate, pressurePlateDown);
        simpleBlockItem(block.get(), pressurePlate);
    }

    public void buttonBlockWithItem(Supplier<? extends ButtonBlock> block, Supplier<? extends Block> baseBlock) {
        ResourceLocation texture = blockTexture(baseBlock);

        buttonBlock(block.get(), texture);
        itemModels().buttonInventory(name(block.get()), texture);
    }

    public void randomRotatedBlock(Supplier<? extends Block> block) {
        randomRotatedBlock(block, cubeAll(block.get()), null);
    }

    public void randomRotatedBlock(Supplier<? extends Block> block, ModelFile model, @Nullable ModelFile mirroredModel) {
        var builder = getVariantBuilder(block.get());
        var partialState = builder.partialState();

        builder.addModels(partialState, makeRotationModels(model, false, true));
        if (mirroredModel != null) {
            builder.addModels(partialState, makeRotationModels(mirroredModel, false, true));
        }
    }

    public void randomRotatedBlockWithItem(Supplier<? extends Block> block) {
        ModelFile model = cubeAll(block.get());
        randomRotatedBlockWithItem(block, model, null);
    }

    public void randomRotatedMirroredBlockWithItem(Supplier<? extends Block> block) {
        randomRotatedMirroredBlockWithItem(block, blockTexture(block));
    }

    public void randomRotatedMirroredBlockWithItem(Supplier<? extends Block> block, ResourceLocation texture) {
        String name = name(block.get());
        ModelFile model = models().cubeAll(name, texture);
        ModelFile mirroredModel = cubeMirroredAll(name, texture);

        randomRotatedBlockWithItem(block, model, mirroredModel);
    }

    public void randomRotatedBlockWithItem(Supplier<? extends Block> block, ModelFile model, @Nullable ModelFile mirrored) {
        randomRotatedBlock(block, model, mirrored);
        simpleBlockItem(block.get(), model);
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

    private String name(Supplier<? extends Block> block) {
        return name(block.get());
    }

    public ResourceLocation blockTexture(Supplier<? extends Block> block) {
        ResourceLocation name = key(block.get());
        return name.withPrefix(ModelProvider.BLOCK_FOLDER + "/");
    }
}
