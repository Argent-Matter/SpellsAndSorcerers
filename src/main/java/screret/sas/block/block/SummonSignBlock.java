package screret.sas.block.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.blockentity.blockentity.SummonSignBE;
import screret.sas.client.particle.ModParticles;

public class SummonSignBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public SummonSignBlock() {
        super(Properties.of(Material.PORTAL).lightLevel((state) -> state.getValue(TRIGGERED) ? 9 : 2).strength(-1.0F, 3600F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, DyeColor.RED).setValue(TRIGGERED, false));
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(COLOR, TRIGGERED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.SUMMON_SIGN_BE.get().create(pPos, pState);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModBlockEntities.SUMMON_SIGN_BE.get(), SummonSignBE::serverTick);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);

        if(pState.getValue(SummonSignBlock.TRIGGERED)){
            Vec3 pos = new Vec3(pPos.getX() + 0.5D, pPos.getY() + 1.0D, pPos.getZ() + 0.5D);
            for (int i = 0; i < 10; ++i){
                pLevel.addParticle(ParticleTypes.ENCHANT, pPos.getX() + pRandom.nextDouble(), pPos.getY() + 2D + pRandom.nextDouble(), pPos.getZ() + pRandom.nextDouble(), 0D, -3D - pRandom.nextDouble(), 0D);
            }
            if(pRandom.nextInt(4) == 0){
                pLevel.addParticle(ModParticles.EYE.get(), true, pos.x, pos.y, pos.z, 0, 1D, 0);
            }
        }


    }
}
