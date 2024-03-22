package screret.sas.constant;

import software.bernie.geckolib.core.animation.RawAnimation;

public class DefaultAnimations {

    public static final RawAnimation ITEM_ON_USE = RawAnimation.begin().thenPlay("item.use");
    public static final RawAnimation RUN = RawAnimation.begin().thenLoop("move.run");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");

}
