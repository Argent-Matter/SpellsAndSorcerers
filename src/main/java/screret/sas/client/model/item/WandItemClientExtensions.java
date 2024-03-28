package screret.sas.client.model.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class WandItemClientExtensions implements IClientItemExtensions {

    public static final HumanoidModel.ArmPose POSE_USE_WAND = HumanoidModel.ArmPose.create("wand", true, (model, entity, arm) -> {
        if (entity.isUsingItem()) {
            switch (arm) {
                case RIGHT -> {
                    model.rightArm.yRot = -0.2F + model.head.yRot;
                    model.rightArm.xRot = -Mth.HALF_PI + model.head.xRot;
                }
                case LEFT -> {
                    model.leftArm.yRot = 0.2F + model.head.yRot;
                    model.leftArm.xRot = -Mth.HALF_PI + model.head.xRot;
                }
            }
        }
    });

    @Override
    public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
        return WandItemClientExtensions.POSE_USE_WAND;
    }
}
