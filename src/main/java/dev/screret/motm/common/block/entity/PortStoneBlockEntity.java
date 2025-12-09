package dev.screret.motm.common.block.entity;

import dev.screret.motm.data.MOTMBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PortStoneBlockEntity extends BlockEntity {

    public PortStoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(MOTMBlockEntities.PORT_STONE.get(), pos, blockState);
    }
}
