package dev.screret.mitm.data;

import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.common.block.PalantirBlock;
import dev.screret.mitm.common.block.PotionDistilleryBlock;
import dev.screret.mitm.common.block.SummoningCircleBlock;
import dev.screret.mitm.common.block.WandTableBlock;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MITMBlocks {

    // Create a Deferred Register to hold Blocks which will all be registered under the "mitm" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.Blocks.createBlocks(MagicOfTheMind.MODID);

    public static final DeferredBlock<WandTableBlock> WAND_TABLE = BLOCKS.register("wand_table", WandTableBlock::new);
    public static final DeferredBlock<Block> SUMMONING_CIRCLE = BLOCKS.register("summoning_circle", SummoningCircleBlock::new);
    public static final DeferredBlock<Block> PALANTIR = BLOCKS.register("palantir", PalantirBlock::new);
    public static final DeferredBlock<Block> POTION_DISTILLERY = BLOCKS.register("potion_distillery", PotionDistilleryBlock::new);

    public static final DeferredBlock<Block> SOULSTEEL_BLOCK = BLOCKS.register("soulsteel_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(5.0F)));
    public static final DeferredBlock<Block> GLINT_ORE = BLOCKS.register("glint_ore",
            () -> new DropExperienceBlock(UniformInt.of(5, 10),
                    BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 9.0F)));
}
