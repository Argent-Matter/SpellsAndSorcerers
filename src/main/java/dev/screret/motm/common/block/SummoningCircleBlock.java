package dev.screret.motm.common.block;

import dev.screret.motm.common.block.entity.SummoningCircleBlockEntity;
import dev.screret.motm.data.MOTMBlockEntities;
import dev.screret.motm.data.MOTMParticles;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mojang.serialization.MapCodec;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SummoningCircleBlock extends BaseEntityBlock {

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public SummoningCircleBlock() {
        super(Properties.ofLegacyCopy(Blocks.END_PORTAL).lightLevel((state) -> state.getValue(TRIGGERED) ? 9 : 2)
                .strength(-1.0F, 3600F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(COLOR, DyeColor.RED).setValue(TRIGGERED, false));
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR, TRIGGERED);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(SummoningCircleBlock::new);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return MOTMBlockEntities.SUMMONING_CIRCLE.get().create(pos, state);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null :
                createTickerHelper(blockEntityType, MOTMBlockEntities.SUMMONING_CIRCLE.get(),
                        SummoningCircleBlockEntity::serverTick);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (state.getValue(SummoningCircleBlock.TRIGGERED)) {
            Vec3 position = Vec3.atLowerCornerWithOffset(pos, 0.5, 3.0, 0.5);
            for (int i = 0; i < 10; ++i) {
                level.addParticle(ParticleTypes.ENCHANT,
                        position.x() + random.nextDouble(), position.y() + 2.0 + random.nextDouble(),
                        position.z() + random.nextDouble(),
                        0.0, -3.0 - random.nextDouble(), 0.0);
            }
            if (random.nextInt(4) == 0) {
                level.addParticle(MOTMParticles.EYE.get(), true,
                        position.x, position.y - 2.0, position.z,
                        0.0, 1.0, 0.0);
            }
        }
    }
}
