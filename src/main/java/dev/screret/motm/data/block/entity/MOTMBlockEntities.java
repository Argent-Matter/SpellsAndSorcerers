package dev.screret.motm.data.block.entity;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.registry.util.*;
import dev.screret.motm.common.block.entity.*;
import dev.screret.motm.data.block.MOTMBlocks;

public class MOTMBlockEntities {

    // spotless:off
    public static final DeferredRegisterHelper.BlockEntities BLOCK_ENTITIES = DeferredRegisterHelper.createBlockEntities(MagicOfTheMind.MOD_ID);

    public static final DeferredBlockEntity<PalantirBlockEntity> PALANTIR = BLOCK_ENTITIES.registerSimpleBlockEntity("palantir", PalantirBlockEntity::new, MOTMBlocks.PALANTIR);

    public static final DeferredBlockEntity<PortStoneBlockEntity> PORT_STONE = BLOCK_ENTITIES.registerSimpleBlockEntity("port_stone", PortStoneBlockEntity::new, MOTMBlocks.PORT_STONE);
    public static final DeferredBlockEntity<MemorystoneBlockEntity> MEMORYSTONE = BLOCK_ENTITIES.registerSimpleBlockEntity("memorystone", MemorystoneBlockEntity::new, MOTMBlocks.MEMORYSTONE);

    // spotless:on
}
