package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.util.registries.DeferredBlockEntity;
import dev.screret.motm.api.util.registries.DeferredRegisterHelper;
import dev.screret.motm.common.block.entity.PalantirBlockEntity;

public class MOTMBlockEntities {

    // spotless:off
    public static final DeferredRegisterHelper.BlockEntities BLOCK_ENTITIES = DeferredRegisterHelper.createBlockEntities(MagicOfTheMind.MOD_ID);

    public static final DeferredBlockEntity<PalantirBlockEntity> PALANTIR = BLOCK_ENTITIES.registerSimpleBlockEntity("palantir", PalantirBlockEntity::new, MOTMBlocks.PALANTIR);

    // spotless:on
}
