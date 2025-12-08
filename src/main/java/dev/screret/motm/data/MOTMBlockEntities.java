package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.util.registries.DeferredBlockEntity;
import dev.screret.motm.api.util.registries.DeferredRegisterHelper;
import dev.screret.motm.common.block.entity.PalantirBlockEntity;
import dev.screret.motm.common.block.entity.PotionDistilleryBlockEntity;
import dev.screret.motm.common.block.entity.SummoningCircleBlockEntity;

public class MOTMBlockEntities {

    // spotless:off
    public static final DeferredRegisterHelper.BlockEntities BLOCK_ENTITIES = DeferredRegisterHelper.createBlockEntities(MagicOfTheMind.MOD_ID);

    public static final DeferredBlockEntity<SummoningCircleBlockEntity> SUMMONING_CIRCLE = BLOCK_ENTITIES.registerSimpleBlockEntity("summoning_circle", SummoningCircleBlockEntity::new, MOTMBlocks.SUMMONING_CIRCLE);
    public static final DeferredBlockEntity<PalantirBlockEntity> PALANTIR = BLOCK_ENTITIES.registerSimpleBlockEntity("palantir", PalantirBlockEntity::new, MOTMBlocks.PALANTIR);
    public static final DeferredBlockEntity<PotionDistilleryBlockEntity> POTION_DISTILLERY = BLOCK_ENTITIES.registerSimpleBlockEntity("potion_distillery", PotionDistilleryBlockEntity::new, MOTMBlocks.POTION_DISTILLERY);

    // spotless:on
}
