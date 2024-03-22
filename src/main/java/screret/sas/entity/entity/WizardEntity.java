package screret.sas.entity.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.config.SASConfig;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.List;

public class WizardEntity extends SpellcasterIllager implements RangedAttackMob, GeoEntity {
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(WizardEntity.class, EntityDataSerializers.BOOLEAN);
    private final float attackRadius = 32, attackRadiusSqr = attackRadius * attackRadius;
    private final ItemStackHandler inventory = new ItemStackHandler(1);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public WizardEntity(EntityType<WizardEntity> type, Level pLevel) {
        super(type, pLevel);
    }

    public boolean isAttacking(){
        return this.entityData.get(IS_ATTACKING);
    }

    public void setAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.ARMOR, 3.0D);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_ATTACKING, false);
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
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @javax.annotation.Nullable SpawnGroupData pSpawnData, @javax.annotation.Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public static List<ItemStack> possibleWands;

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        if(possibleWands == null){
            if(Util.customWands.isEmpty()){
                Util.addItems();
            }
            possibleWands = Util.customWands.values().stream().toList();
        }
        this.setItemSlot(EquipmentSlot.MAINHAND, possibleWands.get(pRandom.nextInt(possibleWands.size() - 1)));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        ListTag listtag = new ListTag();

        for(int i = 0; i < this.inventory.getSlots(); ++i) {
            ItemStack itemstack = this.inventory.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                listtag.add(itemstack.save(new CompoundTag()));
            }
        }

        pCompound.put("Inventory", listtag);
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        ListTag listtag = pCompound.getList("Inventory", 10);

        for(int i = 0; i < listtag.size(); ++i) {
            ItemStack itemstack = ItemStack.of(listtag.getCompound(i));
            if (!itemstack.isEmpty()) {
                this.inventory.insertItem(i, itemstack, true);
            }
        }

        this.setCanPickUpLoot(true);
    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse) {

    }

    @Override
    public boolean hasPatrolTarget() {
        return super.hasPatrolTarget();
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND || pSlot == EquipmentSlot.OFFHAND) {
            return 0.025f;
        }
        return 0.0f;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if(SASConfig.Server.dropWandCores.get()){
            var toDrop = Util.getMainAbilityFromStack(this.getMainHandItem()).get();
            while (toDrop.getChildren() != null && toDrop.getChildren().size() > 0) {
                toDrop = toDrop.getChildren().get(0);
            }
            ItemEntity itementity = this.spawnAtLocation(Util.customWandCores.get(toDrop.getId()).copy());
            if (itementity != null) {
                itementity.setExtendedLifetime();
            }
        }
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

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.EVOKER_CELEBRATE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, 10, state -> state.setAndContinue(this.isAttacking() ? DefaultAnimations.ATTACK_CAST : DefaultAnimations.IDLE))
                        /*.setParticleKeyframeHandler(state -> {
                            var handWorldPos = state.getKeyframeData().script()("rightArm").get().getWorldPosition();
                            this.level.addParticle(Util.getMainAbilityFromStack(animatable.getMainHandItem()).get().getAbility().getParticle(),
                                    handWorldPos.x,
                                    handWorldPos.y,
                                    handWorldPos.z,
                                    (animatable.getRandom().nextDouble() - 0.5D), -animatable.getRandom().nextDouble(),
                                    (animatable.getRandom().nextDouble() - 0.5D));
                        })*/,
                DefaultAnimations.genericWalkController(this),
                DefaultAnimations.genericIdleController(this)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pVelocity) {
        if(Util.getMainAbilityFromStack(this.getMainHandItem()).isPresent()){
            Util.getMainAbilityFromStack(this.getMainHandItem()).get().execute(this.level(), this, this.getMainHandItem(), new WandAbilityInstance.Vec3Wrapped(this.getEyePosition()), 50);
        }
    }

    public class WizardSpellGoal extends SpellcasterUseSpellGoal {

        @Override
        protected void performSpellCasting() {
            var target = WizardEntity.this.getTarget();
            double distanceSqr = WizardEntity.this.distanceToSqr(target);

            float maxDistance = (float)Math.sqrt(distanceSqr) / WizardEntity.this.attackRadiusSqr;
            float distanceFactor = Mth.clamp(maxDistance, 0.1F, 1.0F);
            WizardEntity.this.performRangedAttack(target, distanceFactor);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Util.getMainAbilityFromStack(WizardEntity.this.getMainHandItem()).isPresent();
        }

        @Override
        public void start() {
            super.start();
            WizardEntity.this.lookAt(WizardEntity.this.getTarget(), WizardEntity.this.getMaxHeadYRot(), WizardEntity.this.getMaxHeadXRot());
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
