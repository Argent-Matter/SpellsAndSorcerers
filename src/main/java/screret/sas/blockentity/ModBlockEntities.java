package screret.sas.blockentity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.blockentity.blockentity.PalantirBlockEntity;
import screret.sas.blockentity.blockentity.PotionDistilleryBlockEntity;
import screret.sas.blockentity.blockentity.SummonSignBlockEntity;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SpellsAndSorcerers.MODID);

    public static final Supplier<BlockEntityType<SummonSignBlockEntity>> SUMMON_SIGN = BLOCK_ENTITIES.register("summon_sign", () -> BlockEntityType.Builder.of(SummonSignBlockEntity::new, ModBlocks.SUMMON_SIGN.get()).build(null));
    public static final Supplier<BlockEntityType<PalantirBlockEntity>> PALANTIR = BLOCK_ENTITIES.register("palantir", () -> BlockEntityType.Builder.of(PalantirBlockEntity::new, ModBlocks.PALANTIR.get()).build(null));
    public static final Supplier<BlockEntityType<PotionDistilleryBlockEntity>> POTION_DISTILLERY = BLOCK_ENTITIES.register("potion_distillery", () -> BlockEntityType.Builder.of(PotionDistilleryBlockEntity::new, ModBlocks.POTION_DISTILLERY.get()).build(null));

}
