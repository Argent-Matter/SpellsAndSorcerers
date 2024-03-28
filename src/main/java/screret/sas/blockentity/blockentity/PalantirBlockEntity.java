package screret.sas.blockentity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import screret.sas.blockentity.ModBlockEntities;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PalantirBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final float MAX_LOOK_X_INCREASE = 3f, MAX_LOOK_Y_INCREASE = 3f;

    public float xRot, yRot;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PalantirBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.PALANTIR.get(), pPos, pBlockState);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                DefaultAnimations.genericIdleController(this)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static void eyeAnimationTick(Level pLevel, BlockPos pPos, BlockState pState, PalantirBlockEntity pBlockEntity) {
        Player player = pLevel.getNearestPlayer((double) pPos.getX() + 0.5D, (double) pPos.getY() + 0.5D, (double) pPos.getZ() + 0.3125D, 3.0D, false);
        if (player != null) {
            double x = player.getX() - ((double) pPos.getX() + 0.5D);
            double y = player.getZ() - ((double) pPos.getZ() + 0.5D);
            double z = player.getEyeY() - ((double) pPos.getY() + 0.3125D);


            double distanceXY = Math.sqrt(x * x + y * y);
            float toX = (float) -(Mth.atan2(z, distanceXY) * Mth.RAD_TO_DEG);
            float toY = (float) -(Mth.atan2(y, x) * Mth.RAD_TO_DEG) + 90.0F;
            pBlockEntity.xRot = pBlockEntity.rotlerp(pBlockEntity.xRot, toX, MAX_LOOK_X_INCREASE);
            pBlockEntity.yRot = pBlockEntity.rotlerp(pBlockEntity.yRot, toY, MAX_LOOK_Y_INCREASE);
        } else {
            pBlockEntity.xRot += 0.2F;
        }

    }

    private float rotlerp(float pAngle, float pTargetAngle, float pMaxIncrease) {
        float f = Mth.wrapDegrees(pTargetAngle - pAngle);
        if (f > pMaxIncrease) {
            f = pMaxIncrease;
        }

        if (f < -pMaxIncrease) {
            f = -pMaxIncrease;
        }

        return pAngle + f;
    }
}
