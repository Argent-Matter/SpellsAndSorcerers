package dev.screret.motm.common.item;

import dev.screret.motm.common.block.PortStoneBlock;
import dev.screret.motm.data.MOTMBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PortStoneBlockItem extends BlockItem {

    public PortStoneBlockItem(Properties properties) {
        super(MOTMBlocks.PORT_STONE.get(), properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        Level level = context.getLevel();
        BlockPos.MutableBlockPos pos = context.getClickedPos().mutable();

        for (PortStoneBlock.Part part : PortStoneBlock.Part.ALL_EXCEPT_BOTTOM) {
            pos.setWithOffset(context.getClickedPos(), 0, part.offsetFromBottom, 0);
            BlockState currentState = level.getFluidState(pos).createLegacyBlock();
            level.setBlock(pos, currentState, Block.UPDATE_ALL_IMMEDIATE | Block.UPDATE_KNOWN_SHAPE);
        }
        return super.placeBlock(context, state);
    }
}
