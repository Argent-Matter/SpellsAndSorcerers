package dev.screret.mitm.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.common.block.entity.PalantirBlockEntity;
import dev.screret.mitm.common.block.entity.PotionDistilleryBlockEntity;
import dev.screret.mitm.common.block.entity.SummonSignBlockEntity;

import java.util.function.Supplier;

public class MITMBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MagicOfTheMind.MODID);

    public static final Supplier<BlockEntityType<SummonSignBlockEntity>> SUMMON_SIGN = BLOCK_ENTITIES.register("summon_sign", () -> BlockEntityType.Builder.of(SummonSignBlockEntity::new, MITMBlocks.SUMMON_SIGN.get()).build(null));
    public static final Supplier<BlockEntityType<PalantirBlockEntity>> PALANTIR = BLOCK_ENTITIES.register("palantir", () -> BlockEntityType.Builder.of(PalantirBlockEntity::new, MITMBlocks.PALANTIR.get()).build(null));
    public static final Supplier<BlockEntityType<PotionDistilleryBlockEntity>> POTION_DISTILLERY = BLOCK_ENTITIES.register("potion_distillery", () -> BlockEntityType.Builder.of(PotionDistilleryBlockEntity::new, MITMBlocks.POTION_DISTILLERY.get()).build(null));

}
