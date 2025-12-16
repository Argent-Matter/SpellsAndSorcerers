package dev.screret.motm.common.memory.animation;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.client.memory.ActiveMemory;
import dev.screret.motm.client.memory.MemoryRenderer;
import dev.screret.motm.core.mixin.geckolib.AnimatableManagerAccessor;
import dev.screret.motm.core.mixin.geckolib.AnimationControllerAccessor;
import dev.screret.motm.core.mixin.geckolib.AnimationProcessorAccessor;

import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.keyframe.AnimationPoint;
import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.state.BoneSnapshot;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

public class MemoryAnimationProcessor extends AnimationProcessor<Memory> {

    public MemoryAnimationProcessor(GeoModel<Memory> model) {
        super(model);
    }

    /**
     * Tick and apply transformations to the model based on the current state of the {@link AnimationController}
     *
     * @param animatable            The animatable object relevant to the animation being played
     * @param model                 The model currently being processed
     * @param animatableManager     The AnimatableManager instance being used for this animation processor
     * @param animTime              The internal tick counter kept by the {@link AnimatableManager} for this animatable
     * @param state                 An {@link AnimationState} instance applied to this render frame
     * @param crashWhenCantFindBone Whether to crash if unable to find a required bone, or to continue with the remaining bones
     */
    public void tickAnimation(Memory animatable, GeoModel<Memory> model, AnimatableManager<Memory> animatableManager,
                              double animTime, AnimationState<Memory> state, boolean crashWhenCantFindBone) {
        ActiveMemory currentMemory = MemoryRenderer.INSTANCE.getCurrentMemory();
        if (currentMemory == null) {
            return;
        }
        Map<String, BoneSnapshot> boneSnapshots = updateBoneSnapshots(animatableManager.getBoneSnapshotCollection());

        for (AnimationController<Memory> controller : animatableManager.getAnimationControllers().values()) {
            if (this.reloadAnimations) {
                controller.forceAnimationReset();
                controller.getBoneAnimationQueues().clear();
            }

            ((AnimationControllerAccessor) controller).motm$setIsJustStarting(animatableManager.isFirstTick());

            state.withController(controller);
            controller.process(model, state, ((AnimationProcessorAccessor) this).motm$getBones(), boneSnapshots, animTime,
                    crashWhenCantFindBone);

            for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values()) {
                GeoBone bone = boneAnimation.bone();
                String entityMarker = bone.getName();
                LivingEntity entity = currentMemory.entities().get(entityMarker);
                if (entity == null) {
                    // log & skip invalid entities
                    MagicOfTheMind.LOGGER.warn("Invalid entity marker (bone name) {} in memory {}",
                            entityMarker, currentMemory.memory().getKey().location());
                    continue;
                }

                BoneSnapshot snapshot = boneSnapshots.get(bone.getName());
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

                AnimationPoint posXPoint = boneAnimation.positionXQueue().poll();
                AnimationPoint posYPoint = boneAnimation.positionYQueue().poll();
                AnimationPoint posZPoint = boneAnimation.positionZQueue().poll();

                if (posXPoint != null && posYPoint != null && posZPoint != null) {
                    entity.setDiscardFriction(true);
                    double posX = EasingType.lerpWithOverride(posXPoint, null);
                    double posY = EasingType.lerpWithOverride(posYPoint, null);
                    double posZ = EasingType.lerpWithOverride(posZPoint, null);
                    // calculate relative movement for the entity based on the last keyframe's movement
                    // if we don't do this, the entities will get out of sync with the animation after the first keyframe.
                    entity.travel(new Vec3(
                            posX - snapshot.getOffsetX(),
                            posY - snapshot.getOffsetY(),
                            posZ - snapshot.getOffsetZ()));
                    bone.updatePosition((float) posX, (float) posY, (float) posZ);
                    snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                    snapshot.startPosAnim();
                    bone.markPositionAsChanged();
                }
            }
        }

        this.reloadAnimations = false;
        double resetTickLength = animatable.getBoneResetTime();

        for (GeoBone bone : getRegisteredBones()) {
            if (!bone.hasPositionChanged()) {
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();
                BoneSnapshot saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isPosAnimInProgress()) {
                    saveSnapshot.stopPosAnim(animTime);
                }
                double percentageReset = resetTickLength == 0 ? 1 :
                        Math.min((animTime - saveSnapshot.getLastResetPositionTick()) / resetTickLength, 1);
                bone.setPosX((float) Mth.lerp(percentageReset, saveSnapshot.getOffsetX(), initialSnapshot.getOffsetX()));
                bone.setPosY((float) Mth.lerp(percentageReset, saveSnapshot.getOffsetY(), initialSnapshot.getOffsetY()));
                bone.setPosZ((float) Mth.lerp(percentageReset, saveSnapshot.getOffsetZ(), initialSnapshot.getOffsetZ()));
                if (percentageReset >= 1) {
                    saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                }
            }
        }

        getRegisteredBones().forEach(GeoBone::resetStateChanges);
        ((AnimatableManagerAccessor) animatableManager).motm$finishFirstTick();
    }

    /**
     * Create new bone {@link BoneSnapshot} based on the bone's initial snapshot for the currently registered {@link GeoBone
     * GeoBones},
     * filtered by the bones already present in the master snapshots map
     *
     * @param snapshots The master bone snapshots map from the related {@link AnimatableManager}
     * @return The input snapshots map, for easy assignment
     */
    private Map<String, BoneSnapshot> updateBoneSnapshots(Map<String, BoneSnapshot> snapshots) {
        for (GeoBone bone : getRegisteredBones()) {
            if (!snapshots.containsKey(bone.getName()))
                snapshots.put(bone.getName(), BoneSnapshot.copy(bone.getInitialSnapshot()));
        }

        return snapshots;
    }
}
