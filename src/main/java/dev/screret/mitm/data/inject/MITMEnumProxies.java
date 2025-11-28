package dev.screret.mitm.data.inject;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

@SuppressWarnings({ "Convert2MethodRef", "FunctionalExpressionCanBeFolded", "RedundantSuppression" })
public class MITMEnumProxies {

    public static final EnumProxy<HumanoidModel.ArmPose> POSE_USE_WAND_PROXY = new EnumProxy<>(HumanoidModel.ArmPose.class,
            true,
            (IArmPoseTransformer) (model, entity, arm) -> {
                if (!entity.isUsingItem()) {
                    return;
                }
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
            });


}
