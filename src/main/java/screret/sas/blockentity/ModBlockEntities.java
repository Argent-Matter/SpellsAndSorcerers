package screret.sas.blockentity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.blockentity.blockentity.PalantirBE;
import screret.sas.blockentity.blockentity.PotionDistilleryBE;
import screret.sas.blockentity.blockentity.SummonSignBE;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SpellsAndSorcerers.MODID);

    public static final Supplier<BlockEntityType<SummonSignBE>> SUMMON_SIGN_BE = BLOCK_ENTITIES.register("summon_sign", () -> BlockEntityType.Builder.of(SummonSignBE::new, ModBlocks.SUMMON_SIGN.get()).build(null));
    public static final Supplier<BlockEntityType<PalantirBE>> PALANTIR_BE = BLOCK_ENTITIES.register("palantir", () -> BlockEntityType.Builder.of(PalantirBE::new, ModBlocks.PALANTIR.get()).build(null));
    public static final Supplier<BlockEntityType<PotionDistilleryBE>> POTION_DISTILLERY_BE = BLOCK_ENTITIES.register("potion_distillery", () -> BlockEntityType.Builder.of(PotionDistilleryBE::new, ModBlocks.POTION_DISTILLERY.get()).build(null));

}
