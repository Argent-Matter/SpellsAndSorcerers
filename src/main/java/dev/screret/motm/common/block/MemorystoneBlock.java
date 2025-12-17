package dev.screret.motm.common.block;

import dev.screret.motm.common.block.entity.MemorystoneBlockEntity;
import dev.screret.motm.common.memory.MemoryHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.serialization.MapCodec;

import org.jetbrains.annotations.Nullable;

public class MemorystoneBlock extends BaseEntityBlock {

    public static final MapCodec<MemorystoneBlock> CODEC = simpleCodec(MemorystoneBlock::new);

    public MemorystoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof MemorystoneBlockEntity blockEntity)) {
            return InteractionResult.PASS;
        }
        if (blockEntity.getMemory() == null) {
            return InteractionResult.PASS;
        }
        MemoryHandler.startMemory(pos, blockEntity.getMemory(), player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MemorystoneBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<MemorystoneBlock> codec() {
        return CODEC;
    }
}
