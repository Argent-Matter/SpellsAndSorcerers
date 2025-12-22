package dev.screret.motm.common.block;

import dev.screret.motm.common.block.entity.PortStoneBlockEntity;
import dev.screret.motm.common.block.entity.PortStoneBlockEntity.PortRune;
import dev.screret.motm.common.util.PortStoneHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mojang.serialization.MapCodec;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class PortStoneBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<PortStoneBlock> CODEC = simpleCodec(PortStoneBlock::new);

    public static final int TOTAL_PART_AMOUNT = 4;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    private static final VoxelShape SHAPE_MIDDLE = Block.box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_MIDDLE_2 = Block.box(2, 0, 3, 14, 16, 14);
    private static final VoxelShape SHAPE_BOTTOM = Shapes.or(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(1, 2, 1, 15, 3, 15),
            SHAPE_MIDDLE);
    private static final VoxelShape SHAPE_TOP = Shapes.or(
            Block.box(0, 13, 0, 16, 15, 16),
            Block.box(1, 15, 1, 15, 16, 15),
            SHAPE_MIDDLE);

    public PortStoneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, Part.BOTTOM)
                .setValue(WATERLOGGED, false)
                .setValue(TRIGGERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, WATERLOGGED, TRIGGERED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        Direction clickedFace = hitResult.getDirection();
        if (clickedFace.getAxis().isVertical()) {
            // only allow entering on the horizontal faces
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof PortStoneBlockEntity portStone)) return InteractionResult.PASS;
        Optional<PortRune> destination = portStone.getDestination(clickedFace);
        if (destination.isPresent()) {
            if (level instanceof ServerLevel serverLevel) {
                DimensionTransition teleport = destination.get().makeTeleport(player, clickedFace, serverLevel);
                if (teleport == null) {
                    return InteractionResult.FAIL;
                }
                player.changeDimension(teleport);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PART)) {
            case BOTTOM -> SHAPE_BOTTOM;
            case MIDDLE_1 -> SHAPE_MIDDLE;
            case MIDDLE_2 -> SHAPE_MIDDLE_2;
            case TOP -> SHAPE_TOP;
        };
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Part part = state.getValue(PART);

        if (facing.getAxis() != Direction.Axis.Y || (part == Part.BOTTOM) != (facing == Direction.UP)) {
            if (part != Part.BOTTOM || facing != Direction.DOWN || state.canSurvive(level, currentPos)) {
                return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
            }
        } else if (facingState.getBlock() instanceof PortStoneBlock && facingState.getValue(PART) != part) {
            return facingState.setValue(PART, part);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() >= level.getMaxBuildHeight() - Part.TOP.offsetFromBottom) {
            return null;
        }
        // check if there's enough free space above the bottom block
        BlockPos.MutableBlockPos mutable = pos.mutable();
        for (Part part : Part.ALL_EXCEPT_BOTTOM) {
            mutable.setWithOffset(pos, 0, part.offsetFromBottom, 0);
            if (!level.getBlockState(mutable).canBeReplaced(context)) {
                return null;
            }
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos.MutableBlockPos abovePos = pos.mutable();
        for (Part part : Part.ALL_EXCEPT_BOTTOM) {
            abovePos.setWithOffset(pos, 0, part.offsetFromBottom, 0);
            level.setBlockAndUpdate(abovePos, DoublePlantBlock.copyWaterloggedFrom(level, abovePos, state.setValue(PART, part)));
        }

        if (level instanceof ServerLevel serverLevel) {
            PortStoneHelper.forceLoadPortStone(serverLevel, pos);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);

        if (level instanceof ServerLevel serverLevel) {
            PortStoneHelper.unLoadPortStone(serverLevel, pos);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Part part = state.getValue(PART);
        if (part == Part.BOTTOM) {
            return super.canSurvive(state, level, pos);
        } else {
            // This function is called during world gen and placement before this block is set,
            // so if we are not 'here', assume it's the pre-check.
            if (!state.is(this)) {
                return super.canSurvive(state, level, pos);
            }

            BlockState belowState = level.getBlockState(pos.below(part.offsetFromBottom));
            return belowState.is(this) && belowState.getValue(PART) == Part.BOTTOM;
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !state.canHarvestBlock(level, pos, player))) {
            preventDropFromNonBottomPart(level, pos, state, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    public static void preventDropFromNonBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        Part part = state.getValue(PART);
        if (part == Part.BOTTOM) {
            return;
        }
        BlockPos.MutableBlockPos below = pos.mutable();
        for (int i = -part.offsetFromTop; i < part.offsetFromBottom - 1; i++) {
            below.setWithOffset(pos, 0, -i, 0);
            BlockState belowState = level.getBlockState(below);
            if (!belowState.is(state.getBlock()) || belowState.getValue(PART) == Part.BOTTOM) {
                continue;
            }

            BlockState newState = belowState.getFluidState().createLegacyBlock();
            level.setBlock(below, newState, Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, below, Block.getId(belowState));
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathType) {
        return switch (pathType) {
            case LAND, AIR -> state.getValue(TRIGGERED);
            case WATER -> false;
        };
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortStoneBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected long getSeed(BlockState state, BlockPos pos) {
        Part part = state.getValue(PortStoneBlock.PART);
        return Mth.getSeed(pos.getX(), pos.below(part.offsetFromBottom).getY(), pos.getZ());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public enum Part implements StringRepresentable {

        BOTTOM(0),
        MIDDLE_1(1),
        MIDDLE_2(2),
        TOP(3);

        public static final Part[] VALUES = values();
        public static final Part[] ALL_EXCEPT_BOTTOM = { MIDDLE_1, MIDDLE_2, TOP };

        public final int offsetFromBottom;
        public final int offsetFromTop;

        Part(int offsetFromBottom) {
            this.offsetFromBottom = offsetFromBottom;
            this.offsetFromTop = TOTAL_PART_AMOUNT - offsetFromBottom - 1;
        }

        public static Part getPartFromBottomOffset(int offsetFromBottom) {
            return switch (offsetFromBottom) {
                case 3 -> Part.TOP;
                case 2 -> Part.MIDDLE_2;
                case 1 -> Part.MIDDLE_1;
                default -> Part.BOTTOM;
            };
        }

        @Override
        public String toString() {
            return this.getSerializedName();
        }

        @Override
        public String getSerializedName() {
            return switch (this) {
                case TOP -> "top";
                case MIDDLE_2 -> "middle_2";
                case MIDDLE_1 -> "middle_1";
                case BOTTOM -> "bottom";
            };
        }
    }
}
