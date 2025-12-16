package dev.screret.motm.api.util.geckolib;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class StatelessAnimationController<T extends GeoAnimatable> extends AnimationController<T> {
    @Nullable
    protected RawAnimation currentAnim = null;

    public StatelessAnimationController(T animatable, String name) {
        super(animatable, name, state -> PlayState.STOP);
    }

    /**
     * Set the current animation for this controller
     * <p>
     * This will be used to handle the {@link AnimationState} at each render pass
     */
    public void setCurrentAnimation(@Nullable RawAnimation animation) {
        this.currentAnim = animation;
    }

    /**
     * Get the current animation state for this controller
     */
    @Nullable
    public RawAnimation getCurrentAnim() {
        return this.currentAnim;
    }

    @Override
    public AnimationStateHandler<T> getStateHandler() {
        return this::overrideStateHandler;
    }

    @ApiStatus.Internal
    protected PlayState overrideStateHandler(AnimationState<T> test) {
        return getCurrentAnim() == null ? PlayState.STOP : test.setAndContinue(getCurrentAnim());
    }
}
