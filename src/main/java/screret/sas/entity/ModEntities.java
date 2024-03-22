package screret.sas.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;
import screret.sas.entity.entity.WizardEntity;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SpellsAndSorcerers.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<WizardEntity>> WIZARD = ENTITY_TYPES.register("wizard", () -> EntityType.Builder.of(WizardEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build(WizardEntity.class.getSimpleName().toLowerCase()));
    public static final DeferredHolder<EntityType<?>, EntityType<BossWizardEntity>> BOSS_WIZARD = ENTITY_TYPES.register("boss_wizard", () -> EntityType.Builder.of(BossWizardEntity::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).clientTrackingRange(8).build(BossWizardEntity.class.getSimpleName().toLowerCase()));
    public static final DeferredHolder<EntityType<?>, EntityType<CthulhuEntity>> CTHULHU = ENTITY_TYPES.register("cthulhu", () -> EntityType.Builder.of(CthulhuEntity::new, MobCategory.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).clientTrackingRange(10).build(BossWizardEntity.class.getSimpleName().toLowerCase()));
}
