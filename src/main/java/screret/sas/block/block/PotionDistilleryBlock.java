package screret.sas.block.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.blockentity.blockentity.PotionDistilleryBE;

public class PotionDistilleryBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public PotionDistilleryBlock() {
        super(Properties.of(Material.NETHER_WOOD).strength(3.0F, 4.0F).sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            NetworkHooks.openScreen((ServerPlayer) pPlayer, this.getMenuProvider(pState, pLevel, pPos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.POTION_DISTILLERY_BE.get().create(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof PotionDistilleryBE distilleryBE) {
                if (pLevel instanceof ServerLevel) {
                    Containers.dropContents(pLevel, pPos, distilleryBE.getInventoryWrapper());
                    distilleryBE.getRecipesToAwardAndPopExperience((ServerLevel)pLevel, Vec3.atCenterOf(pPos));
                }

                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        var be = pLevel.getBlockEntity(pPos, ModBlockEntities.POTION_DISTILLERY_BE.get());
        return be.map(distilleryBE -> getRedstoneSignalFromItemHandler(distilleryBE.getInventory())).orElse(0);
    }

    public static int getRedstoneSignalFromItemHandler(ItemStackHandler handler) {
        if (handler == null) {
            return 0;
        } else {
            int power = 0;
            float slotCount = 0.0F;

            for(int j = 0; j < handler.getSlots(); ++j) {
                ItemStack itemstack = handler.getStackInSlot(j);
                if (!itemstack.isEmpty()) {
                    slotCount += (float)itemstack.getCount() / (float)Math.min(handler.getSlotLimit(j), itemstack.getMaxStackSize());
                    ++power;
                }
            }

            slotCount /= (float)handler.getSlots();
            return Mth.floor(slotCount * 14.0F) + (power > 0 ? 1 : 0);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.POTION_DISTILLERY_BE.get(), PotionDistilleryBE::serverTick);
    }

}
