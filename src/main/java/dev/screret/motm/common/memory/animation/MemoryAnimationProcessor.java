package dev.screret.motm.common.memory.animation;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.Memory;
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

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

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
     * @param animationState        An {@link AnimationState} instance applied to this render frame
     * @param crashWhenCantFindBone Whether to crash if unable to find a required bone, or to continue with the remaining bones
     */
    public void tickAnimation(Memory animatable, GeoModel<Memory> model, AnimatableManager<Memory> animatableManager,
                              double animTime, AnimationState<Memory> animationState, boolean crashWhenCantFindBone) {
        Holder<Memory> currentMemory = MemoryRenderer.INSTANCE.getCurrentMemory();
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

            animationState.withController(controller);
            controller.process(model, animationState, ((AnimationProcessorAccessor) this).motm$getBones(), boneSnapshots,
                    animTime,
                    crashWhenCantFindBone);

            double adjustedTick = ((AnimationControllerAccessor) controller).motm$adjustTick(animTime);

            for (BoneAnimationQueue boneAnimation : controller.getBoneAnimationQueues().values()) {
                GeoBone bone = boneAnimation.bone();

                BoneSnapshot snapshot = boneSnapshots.get(bone.getName());
                BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

                AnimationPoint posXPoint = boneAnimation.positionXQueue().poll();
                AnimationPoint posYPoint = boneAnimation.positionYQueue().poll();
                AnimationPoint posZPoint = boneAnimation.positionZQueue().poll();

                if (posXPoint != null && posYPoint != null && posZPoint != null) {
                    double posX = EasingType.lerpWithOverride(posXPoint, null);
                    double posY = EasingType.lerpWithOverride(posYPoint, null);
                    double posZ = EasingType.lerpWithOverride(posZPoint, null);

                    bone.updatePosition((float) posX, (float) posY, (float) posZ);
                    snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                    snapshot.startPosAnim();
                    bone.markPositionAsChanged();

                    Set<Entity> entities = MemoryRenderer.INSTANCE.getEntities().get(bone.getName());
                    if (!checkEntitiesNotNull(entities, bone.getName(), currentMemory, crashWhenCantFindBone)) {
                        continue;
                    }
                    for (Entity entity : entities) {
                        // only update entities' positions every full tick.
                        if (!Mth.equal(adjustedTick, 0.0d)) {
                            continue;
                        }
                        if (entity instanceof LivingEntity livingEntity) {
                            livingEntity.setDiscardFriction(true);
                        }
                        // set relative movement for the entity based on the keyframe's relative movement
                        // if we don't do this, the entities will get out of sync with the animation after the first keyframe.
                        entity.setDeltaMovement(posX - posXPoint.animationStartValue(),
                                posY - posYPoint.animationStartValue(),
                                posZ - posZPoint.animationStartValue());
                    }
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
                double backX = Mth.lerp(percentageReset, saveSnapshot.getOffsetX(), initialSnapshot.getOffsetX());
                double backY = Mth.lerp(percentageReset, saveSnapshot.getOffsetY(), initialSnapshot.getOffsetY());
                double backZ = Mth.lerp(percentageReset, saveSnapshot.getOffsetZ(), initialSnapshot.getOffsetZ());
                bone.updatePosition((float) backX, (float) backY, (float) backZ);

                if (percentageReset >= 1) {
                    saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                }
                Set<Entity> entities = MemoryRenderer.INSTANCE.getEntities().get(bone.getName());
                if (!checkEntitiesNotNull(entities, bone.getName(), currentMemory, crashWhenCantFindBone)) {
                    continue;
                }
                for (Entity entity : entities) {
                    entity.setPos(backX, backY, backZ);
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

    private static boolean checkEntitiesNotNull(@Nullable Set<Entity> entities, String boneName, Holder<Memory> memory,
                                                boolean crashWhenCantFindBone) {
        // log & skip invalid entities
        if (entities == null) {
            if (crashWhenCantFindBone) {
                throw new RuntimeException("Invalid entity tag (bone name) %s in memory %s (no entities have that tag)"
                        .formatted(boneName, memory.getKey().location()));
            } else {
                MagicOfTheMind.LOGGER.warn("Invalid entity tag (bone name) {} in memory {} (no entities have that tag)",
                        boneName, memory.getKey().location());
            }
            return false;
        }
        return true;
    }
}
