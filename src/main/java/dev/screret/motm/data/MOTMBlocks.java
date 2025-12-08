package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.block.PalantirBlock;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMBlocks {

    // spotless:off
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.Blocks.createBlocks(MagicOfTheMind.MOD_ID);

    public static final DeferredBlock<PalantirBlock> PALANTIR = BLOCKS.register("palantir", PalantirBlock::new);

    public static final DeferredBlock<Block> SOULSTEEL_BLOCK = BLOCKS.registerSimpleBlock("soulsteel_block", BlockBehaviour.Properties.of().strength(5.0F));
    public static final DeferredBlock<Block> GLINT_ORE = BLOCKS.register("glint_ore",
            () -> new DropExperienceBlock(UniformInt.of(5, 10), BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 9.0F)));

    // spotless:on
}
