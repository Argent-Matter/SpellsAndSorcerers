package dev.screret.motm.common.block.entity;

import dev.screret.motm.data.block.entity.MOTMBlockEntities;

import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PalantirBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final float MAX_LOOK_X_INCREASE = 3f, MAX_LOOK_Y_INCREASE = 3f;

    public float xRot, yRot;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PalantirBlockEntity(BlockPos pos, BlockState blockState) {
        super(MOTMBlockEntities.PALANTIR.get(), pos, blockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static void eyeAnimationTick(Level level, BlockPos pos, BlockState state, PalantirBlockEntity blockEntity) {
        Player player = level.getNearestPlayer((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D,
                (double) pos.getZ() + 0.3125D, 3.0D, false);
        if (player != null) {
            double x = player.getX() - ((double) pos.getX() + 0.5D);
            double y = player.getZ() - ((double) pos.getZ() + 0.5D);
            double z = player.getEyeY() - ((double) pos.getY() + 0.3125D);

            double distanceXY = Math.sqrt(x * x + y * y);
            float toX = (float) -(Mth.atan2(z, distanceXY) * Mth.RAD_TO_DEG);
            float toY = (float) -(Mth.atan2(y, x) * Mth.RAD_TO_DEG) + 90.0F;
            blockEntity.xRot = blockEntity.rotlerp(blockEntity.xRot, toX, MAX_LOOK_X_INCREASE);
            blockEntity.yRot = blockEntity.rotlerp(blockEntity.yRot, toY, MAX_LOOK_Y_INCREASE);
        } else {
            blockEntity.xRot += 0.2F;
        }
    }

    private float rotlerp(float angle, float targetAngle, float maxIncrease) {
        float f = Mth.wrapDegrees(targetAngle - angle);
        if (f > maxIncrease) {
            f = maxIncrease;
        }

        if (f < -maxIncrease) {
            f = -maxIncrease;
        }

        return angle + f;
    }
}
