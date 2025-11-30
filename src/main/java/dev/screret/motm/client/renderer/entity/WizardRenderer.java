package dev.screret.motm.client.renderer.entity;

import dev.screret.mitm.client.model.entity.WizardModel;
import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.entity.WizardEntity;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector3d;

import org.jetbrains.annotations.Nullable;

public class WizardRenderer extends GeoEntityRenderer<WizardEntity> {

    public WizardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WizardModel());
    }

    @Override
    public void postRender(PoseStack poseStack, WizardEntity animatable, BakedGeoModel model,
                           MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
                           float partialTick, int packedLight, int packedOverlay, int colour) {
        if (model.getBone("rightArm").isEmpty() || MOTMUtil.getMainAbilityFromStack(animatable.getMainHandItem()).isEmpty()) {
            return;
        }
        if (!animatable.isCastingSpell()) {
            return;
        }
        Vector3d handWorldPos = model.getBone("rightArm").get().getWorldPosition();
        animatable.level().addParticle(
                MOTMUtil.getMainAbilityFromStack(animatable.getMainHandItem()).get().getAbility().getParticle(),
                handWorldPos.x, handWorldPos.y, handWorldPos.z,
                animatable.getRandom().nextDouble() - 0.5D,
                -animatable.getRandom().nextDouble(),
                animatable.getRandom().nextDouble() - 0.5D);
    }
}
