package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.util.registries.DeferredBlockEntity;
import dev.screret.motm.api.util.registries.DeferredRegisterHelper;
import dev.screret.motm.common.block.entity.PalantirBlockEntity;
import dev.screret.motm.common.block.entity.PortStoneBlockEntity;

public class MOTMBlockEntities {

    // spotless:off
    public static final DeferredRegisterHelper.BlockEntities BLOCK_ENTITIES = DeferredRegisterHelper.createBlockEntities(MagicOfTheMind.MOD_ID);

    public static final DeferredBlockEntity<PalantirBlockEntity> PALANTIR = BLOCK_ENTITIES.registerSimpleBlockEntity("palantir", PalantirBlockEntity::new, MOTMBlocks.PALANTIR);

    public static final DeferredBlockEntity<PortStoneBlockEntity> PORT_STONE = BLOCK_ENTITIES.registerSimpleBlockEntity("port_stone", PortStoneBlockEntity::new, MOTMBlocks.PORT_STONE);

    // spotless:on
}
