package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.block.MemorystoneBlock;
import dev.screret.motm.common.block.PalantirBlock;
import dev.screret.motm.common.block.PortStoneBlock;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMBlocks {

    // spotless:off
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.Blocks.createBlocks(MagicOfTheMind.MOD_ID);

    public static final DeferredBlock<PalantirBlock> PALANTIR = BLOCKS.register("palantir", PalantirBlock::new);

    public static final DeferredBlock<Block> SOULSTEEL_BLOCK = BLOCKS.registerSimpleBlock("soulsteel_block", BlockBehaviour.Properties.of().strength(5));

    public static final DeferredBlock<DropExperienceBlock> GLINT_ORE = BLOCKS.registerBlock("glint_ore", p -> new DropExperienceBlock(UniformInt.of(5, 10), p),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3, 9));

    public static final DeferredBlock<MemorystoneBlock> MEMORYSTONE = BLOCKS.registerBlock("memorystone", MemorystoneBlock::new,
            hardBlackBlockProperties().strength(12, 9));
    // TODO (maybe) make it so this can be awakened somehow (turned into actual memorystone)
    public static final DeferredBlock<Block> UNAWAKENED_MEMORYSTONE = BLOCKS.registerSimpleBlock("unawakened_memorystone",
            hardBlackBlockProperties().strength(12, 9));

    public static final DeferredBlock<PortStoneBlock> PORT_STONE = BLOCKS.registerBlock("port_stone", PortStoneBlock::new,
            hardBlackBlockProperties().strength(75, 1600));

    // spotless:on

    private static BlockBehaviour.Properties hardBlackBlockProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops();
    }
}
