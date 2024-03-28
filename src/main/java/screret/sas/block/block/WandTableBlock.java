package screret.sas.block.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import org.jetbrains.annotations.Nullable;
import screret.sas.client.particle.ModParticles;
import screret.sas.container.container.WandTableMenu;

public class WandTableBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    private static final Component CONTAINER_TITLE = Component.translatable("container.sas.wand_table");


    public WandTableBlock() {
        super(Properties.ofLegacyCopy(Blocks.STONE).mapColor(MapColor.SAND).strength(3.0F, 36.0F).lightLevel((function) -> 7));
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);

        if (pRandom.nextInt(16) == 0) {
            pLevel.addParticle(ParticleTypes.ENCHANT, pPos.getX() + 0.5D, pPos.getY() + 2.0D, pPos.getZ() + 0.5D, pPos.getX() - pRandom.nextFloat() + 0.25D, pPos.getY() - pRandom.nextFloat() + 1.0D, pPos.getZ() - pRandom.nextFloat() + 0.25D);
        }
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        pLevel.scheduleTick(pPos, this, 0);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.sendParticles(ModParticles.EYE.get(), pPos.getX() + 0.5F, pPos.getY() + 1F, pPos.getZ() + 0.5F, 1, 0, 0, 0, 0);
        pLevel.scheduleTick(pPos, this, 20, TickPriority.NORMAL);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((windowId, playerInventory, player) -> new WandTableMenu(windowId, playerInventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            pPlayer.openMenu(pState.getMenuProvider(pLevel, pPos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }
}
