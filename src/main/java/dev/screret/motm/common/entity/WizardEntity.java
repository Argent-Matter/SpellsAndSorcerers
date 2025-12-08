package dev.screret.motm.common.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WizardEntity extends SpellcasterIllager implements RangedAttackMob, GeoEntity {

    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(WizardEntity.class,
            EntityDataSerializers.BOOLEAN);
    private final float attackRadius = 32, attackRadiusSqr = attackRadius * attackRadius;
    private final ItemStackHandler inventory = new ItemStackHandler(1);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WizardEntity(EntityType<WizardEntity> type, Level level) {
        super(type, level);
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ARMOR, 3.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_ATTACKING, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SpellcasterCastingSpellGoal());
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(2, new WizardSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3,
                new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ListTag listtag = new ListTag();

        for (int i = 0; i < this.inventory.getSlots(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                listtag.add(itemstack.save(this.registryAccess(), new CompoundTag()));
            }
        }

        compound.put("Inventory", listtag);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ListTag listtag = compound.getList("Inventory", 10);

        for (int i = 0; i < listtag.size(); ++i) {
            ItemStack itemstack = ItemStack.CODEC
                    .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), listtag.getCompound(i))
                    .getOrThrow();
            if (!itemstack.isEmpty()) {
                this.inventory.insertItem(i, itemstack, true);
            }
        }

        this.setCanPickUpLoot(true);
    }

    @Override
    public void applyRaidBuffs(ServerLevel level, int wave, boolean unused) {}

    @Override
    public boolean hasPatrolTarget() {
        return super.hasPatrolTarget();
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
            return 0.025f;
        }
        return 0.0f;
    }

    @Override
    protected SoundEvent getCastingSoundEvent() {
        return SoundEvents.GHAST_SCREAM;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.EVOKER_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, 10,
                        state -> state
                                .setAndContinue(this.isAttacking() ? DefaultAnimations.ATTACK_CAST : DefaultAnimations.IDLE)),
                DefaultAnimations.genericWalkController(this),
                DefaultAnimations.genericIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
    }

    public class WizardSpellGoal extends SpellcasterUseSpellGoal {

        @Override
        protected void performSpellCasting() {
            var target = WizardEntity.this.getTarget();
            double distanceSqr = WizardEntity.this.distanceToSqr(target);

            float maxDistance = (float) Math.sqrt(distanceSqr) / WizardEntity.this.attackRadiusSqr;
            float distanceFactor = Mth.clamp(maxDistance, 0.1F, 1.0F);
            WizardEntity.this.performRangedAttack(target, distanceFactor);
        }

        @Override
        public boolean canUse() {
            return super.canUse();
        }

        @Override
        public void start() {
            super.start();
            WizardEntity.this.lookAt(WizardEntity.this.getTarget(), WizardEntity.this.getMaxHeadYRot(),
                    WizardEntity.this.getMaxHeadXRot());
            WizardEntity.this.setAttacking(true);
        }

        @Override
        public void stop() {
            super.stop();
            WizardEntity.this.setAttacking(false);
        }

        @Override
        protected int getCastingTime() {
            return 15;
        }

        @Override
        protected int getCastingInterval() {
            return 80;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected IllagerSpell getSpell() {
            return IllagerSpell.DISAPPEAR;
        }
    }
}
