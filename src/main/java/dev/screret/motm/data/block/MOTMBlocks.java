package dev.screret.motm.data.block;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.block.MemorystoneBlock;
import dev.screret.motm.common.block.PalantirBlock;
import dev.screret.motm.common.block.PortStoneBlock;

import net.minecraft.core.Holder;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class MOTMBlocks {

    // spotless:off
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.Blocks.createBlocks(MagicOfTheMind.MOD_ID);

    public static final DeferredBlock<PalantirBlock> PALANTIR = BLOCKS.register("palantir", PalantirBlock::new);

    public static final DeferredBlock<Block> SOULSTEEL_BLOCK = BLOCKS.registerSimpleBlock("soulsteel_block", BlockBehaviour.Properties.of().strength(5));

    public static final DeferredBlock<DropExperienceBlock> GLINT_ORE = BLOCKS.registerBlock("glint_ore", p -> new DropExperienceBlock(UniformInt.of(5, 10), p),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3f, 9f));

    public static final DeferredBlock<PortStoneBlock> PORT_STONE = BLOCKS.registerBlock("port_stone", PortStoneBlock::new,
            memoryStoneProperties().strength(75f, 1600f).pushReaction(PushReaction.BLOCK));

    // region memorystone block set

    // TODO (maybe) make it so this can be awakened somehow (turned into actual memorystone)
    public static final DeferredBlock<Block> UNAWAKENED_MEMORYSTONE = BLOCKS.registerSimpleBlock("unawakened_memorystone", unawakenedMemoryStoneProperties());
    public static final DeferredBlock<SlabBlock> UNAWAKENED_MEMORYSTONE_SLAB = BLOCKS.registerBlock("unawakened_memorystone_slab", SlabBlock::new, unawakenedMemoryStoneProperties());
    public static final DeferredBlock<StairBlock> UNAWAKENED_MEMORYSTONE_STAIRS = BLOCKS.registerBlock("unawakened_memorystone_stairs", stair(UNAWAKENED_MEMORYSTONE), unawakenedMemoryStoneProperties());
    public static final DeferredBlock<WallBlock> UNAWAKENED_MEMORYSTONE_WALL = BLOCKS.registerBlock("unawakened_memorystone_wall", WallBlock::new, unawakenedMemoryStoneProperties().forceSolidOn());

    public static final DeferredBlock<MemorystoneBlock> MEMORYSTONE = BLOCKS.registerBlock("memorystone", MemorystoneBlock::new, memoryStoneProperties());
    public static final DeferredBlock<StairBlock> MEMORYSTONE_STAIRS = BLOCKS.registerBlock("memorystone_stairs", stair(MEMORYSTONE), memoryStoneProperties());
    public static final DeferredBlock<SlabBlock> MEMORYSTONE_SLAB = BLOCKS.registerBlock("memorystone_slab", SlabBlock::new, polishedMemoryStoneProperties());
    public static final DeferredBlock<WallBlock> MEMORYSTONE_WALL = BLOCKS.registerBlock("memorystone_wall", WallBlock::new, memoryStoneProperties().forceSolidOn());

    public static final DeferredBlock<MemorystoneBlock> POLISHED_MEMORYSTONE = BLOCKS.registerBlock("polished_memorystone", MemorystoneBlock::new, polishedMemoryStoneProperties());
    public static final DeferredBlock<StairBlock> POLISHED_MEMORYSTONE_STAIRS = BLOCKS.registerBlock("polished_memorystone_stairs", stair(POLISHED_MEMORYSTONE), polishedMemoryStoneProperties());
    public static final DeferredBlock<SlabBlock> POLISHED_MEMORYSTONE_SLAB = BLOCKS.registerBlock("polished_memorystone_slab", SlabBlock::new, polishedMemoryStoneProperties());
    public static final DeferredBlock<WallBlock> POLISHED_MEMORYSTONE_WALL = BLOCKS.registerBlock("polished_memorystone_wall", WallBlock::new, polishedMemoryStoneProperties().forceSolidOn());
    public static final DeferredBlock<PressurePlateBlock> POLISHED_MEMORYSTONE_PRESSURE_PLATE = BLOCKS.registerBlock("polished_memorystone_pressure_plate",
            p -> new PressurePlateBlock(MOTMBlockSetTypes.POLISHED_MEMORYSTONE, p),
            memoryStoneProperties().forceSolidOn().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)
    );
    public static final DeferredBlock<ButtonBlock> POLISHED_MEMORYSTONE_BUTTON = BLOCKS.registerBlock("polished_memorystone_button", stoneButton(MOTMBlockSetTypes.POLISHED_MEMORYSTONE));
    public static final DeferredBlock<MemorystoneBlock> CHISELED_POLISHED_MEMORYSTONE = BLOCKS.registerBlock("chiseled_polished_memorystone", MemorystoneBlock::new, memoryStoneProperties());

    public static final DeferredBlock<MemorystoneBlock> POLISHED_MEMORYSTONE_BRICKS = BLOCKS.registerBlock("polished_memorystone_bricks", MemorystoneBlock::new, memoryStoneProperties());
    public static final DeferredBlock<Block> CRACKED_POLISHED_MEMORYSTONE_BRICKS = BLOCKS.registerSimpleBlock("cracked_polished_memorystone_bricks", polishedMemoryStoneProperties());
    public static final DeferredBlock<StairBlock> POLISHED_MEMORYSTONE_BRICK_STAIRS = BLOCKS.registerBlock("polished_memorystone_brick_stairs", stair(POLISHED_MEMORYSTONE_BRICKS), memoryStoneProperties());
    public static final DeferredBlock<SlabBlock> POLISHED_MEMORYSTONE_BRICK_SLAB = BLOCKS.registerBlock("polished_memorystone_brick_slab", SlabBlock::new, polishedMemoryStoneProperties());
    public static final DeferredBlock<WallBlock> POLISHED_MEMORYSTONE_BRICK_WALL = BLOCKS.registerBlock("polished_memorystone_brick_wall", WallBlock::new, polishedMemoryStoneProperties().forceSolidOn());

    // endregion

    // spotless:on

    private static BlockBehaviour.Properties memoryStoneProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .sound(SoundType.GILDED_BLACKSTONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .strength(12f, 9f)
                .requiresCorrectToolForDrops();
    }

    private static BlockBehaviour.Properties unawakenedMemoryStoneProperties() {
        return memoryStoneProperties()
                .sound(SoundType.STONE)
                .strength(9f, 9f);
    }

    private static BlockBehaviour.Properties polishedMemoryStoneProperties() {
        return memoryStoneProperties()
                .strength(16f, 9f);
    }

    private static Function<BlockBehaviour.Properties, StairBlock> stair(Holder<Block> baseBlock) {
        return p -> new StairBlock(baseBlock.value().defaultBlockState(), p);
    }

    private static Function<BlockBehaviour.Properties, ButtonBlock> stoneButton(BlockSetType blockSetType) {
        return p -> new ButtonBlock(blockSetType, 20, p);
    }
}
