package screret.sas.block.block;

import com.mojang.serialization.MapCodec;
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
import org.jetbrains.annotations.Nullable;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.blockentity.blockentity.PalantirBlockEntity;

public class PalantirBlock extends BaseEntityBlock {
    private static final VoxelShape BASE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 2.0D, 12.0D);
    private static final VoxelShape TOP = Block.box(5.0D, 2.0D, 5.0D, 11.0D, 8.0D, 11.0D);
    protected static final VoxelShape SHAPE = Shapes.or(BASE, TOP);


    public PalantirBlock() {
        super(Properties.ofLegacyCopy(Blocks.TINTED_GLASS).sound(SoundType.AMETHYST).lightLevel((state) -> 2).noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(PalantirBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.PALANTIR.get().create(pPos, pState);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, ModBlockEntities.PALANTIR.get(), PalantirBlockEntity::eyeAnimationTick) : null;
    }
}
