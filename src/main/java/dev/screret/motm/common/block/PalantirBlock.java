package dev.screret.motm.common.block;

import dev.screret.motm.common.block.entity.PalantirBlockEntity;
import dev.screret.motm.data.block.entity.MOTMBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mojang.serialization.MapCodec;

import org.jetbrains.annotations.Nullable;

public class PalantirBlock extends BaseEntityBlock {

    public static final MapCodec<PalantirBlock> CODEC = MapCodec.unit(PalantirBlock::new);

    private static final VoxelShape BASE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D);
    private static final VoxelShape TOP = Block.box(5.0D, 2.0D, 5.0D, 11.0D, 8.0D, 11.0D);
    protected static final VoxelShape SHAPE = Shapes.or(BASE, TOP);

    public PalantirBlock() {
        super(Properties.ofLegacyCopy(Blocks.TINTED_GLASS).sound(SoundType.AMETHYST).lightLevel((state) -> 2).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PalantirBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                            BlockEntityType<T> blockEntityType) {
        return level.isClientSide ?
                createTickerHelper(blockEntityType, MOTMBlockEntities.PALANTIR.get(), PalantirBlockEntity::eyeAnimationTick) :
                null;
    }
}
