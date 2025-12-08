package dev.screret.motm.common.entity;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.ability.WandAbilityInstance;
import dev.screret.motm.common.block.entity.SummoningCircleBlockEntity;
import dev.screret.motm.common.entity.goal.ShootEnemyGoal;
import dev.screret.motm.config.MOTMConfig;
import dev.screret.motm.data.MOTMWandAbilities;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;

import java.util.EnumSet;
import java.util.function.Predicate;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BossWizardEntity extends Monster implements RangedAttackMob, GeoEntity {

    public static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (mob) -> (!mob.getType()
            .is(EntityTypeTags.ILLAGER_FRIENDS) && mob.attackable());

    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BossWizardEntity.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> INVULNERABLE_TICKS = SynchedEntityData.defineId(BossWizardEntity.class,
            EntityDataSerializers.INT);
    private static final int MAX_INVULNERABLE_TICKS = 75;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected int spellCastingTickCount;
    private @Nullable WandAbilityInstance currentSpell = null;
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN,
            BossEvent.BossBarOverlay.PROGRESS);
    private BlockPos spawnPos;

    public BossWizardEntity(EntityType<BossWizardEntity> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setHealth(this.getMaxHealth());
        this.setItemSlot(EquipmentSlot.MAINHAND, createBossWand());
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(true);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setIsAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }

    public void setSpawningPosition(BlockPos pos) {
        this.spawnPos = pos;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ARMOR, 7.5D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_ATTACKING, false);
        builder.define(INVULNERABLE_TICKS, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new WizardDoNothingGoal());
        this.goalSelector.addGoal(2, new ShootEnemyGoal(this, 1.0D, 100, 32.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2,
                new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false, LIVING_ENTITY_SELECTOR));
    }

    @Override
    protected void customServerAiStep() {
        if (this.getInvulnerableTicks() > 0) {
            if (!(this.level().getBlockEntity(this.spawnPos) instanceof SummoningCircleBlockEntity)) {
                this.discard();
            }
            if (this.level().getBlockState(this.spawnPos.above(2)) != Blocks.AIR.defaultBlockState()) {
                this.level().setBlockAndUpdate(this.spawnPos.above(2), Blocks.AIR.defaultBlockState());
                this.level().setBlockAndUpdate(this.spawnPos.above(), Blocks.AIR.defaultBlockState());

            }

            int ticks = this.getInvulnerableTicks() - 1;
            this.bossEvent.setProgress(1.0F - (float) ticks / MAX_INVULNERABLE_TICKS);
            if (ticks <= 0) {
                // Explosion.BlockInteraction explosion = ForgeEventFactory.getMobGriefingEvent(this.level, this) ?
                // Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
                // this.level().explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, explosion);
                this.level().setBlockAndUpdate(this.spawnPos, Blocks.AIR.defaultBlockState());
                this.setInvulnerable(false);
                if (!this.isSilent()) {
                    this.level().globalLevelEvent(LevelEvent.SOUND_WITHER_BOSS_SPAWN, this.blockPosition(), 0);
                }
            }

            this.setInvulnerableTicks(ticks);
            if (this.tickCount % 2 == 0) {
                this.heal(10.0F);
            }

        } else {
            super.customServerAiStep();

            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @SuppressWarnings("deprecation") // override-only
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, createBossWand());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source != damageSources().drown() && !(source.getEntity() instanceof BossWizardEntity)) {
            if (this.getInvulnerableTicks() > 0 && source != damageSources().fellOutOfWorld()) {
                return false;
            } else {
                Entity sourceEntity = source.getEntity();
                if (!(sourceEntity instanceof Player) &&
                        sourceEntity instanceof LivingEntity living &&
                        living.getType().is(EntityTypeTags.ILLAGER_FRIENDS)) {
                    return false;
                } else {
                    return super.hurt(source, amount);
                }
            }
        } else {
            return false;
        }
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlot slot) {
        return 0.0F;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        if (!MOTMConfig.Server.dropWandCores.get()) {
            return;
        }
        var toDrop = MOTMUtil.getMainAbilityFromStack(this.getMainHandItem()).get();
        while (!toDrop.getChildren().isEmpty()) {
            toDrop = toDrop.getChildren().getFirst();
        }
        ItemEntity itemEntity = this.spawnAtLocation(MOTMUtil.CUSTOM_WAND_CORES.get(toDrop.getId()).copy());
        if (itemEntity != null) {
            itemEntity.setExtendedLifetime();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("invulnerable_ticks", this.getInvulnerableTicks());
        if (this.currentSpell != null) {
            compound.put("current_spell", WandAbilityInstance.CODEC
                    .encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.currentSpell)
                    .getOrThrow());
        }
        compound.put("spawn_pos", NbtUtils.writeBlockPos(this.spawnPos));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setInvulnerableTicks(compound.getInt("invulnerable_ticks"));
        if (compound.contains("current_spell")) {
            this.currentSpell = WandAbilityInstance.CODEC
                    .parse(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get("current_spell"))
                    .result().orElse(null);
        }
        NbtUtils.readBlockPos(compound, "spawn_pos").ifPresent(pos -> this.spawnPos = pos);

        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    public int getInvulnerableTicks() {
        return this.entityData.get(INVULNERABLE_TICKS);
    }

    public void setInvulnerableTicks(int invulnerableTicks) {
        this.entityData.set(INVULNERABLE_TICKS, invulnerableTicks);
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(MAX_INVULNERABLE_TICKS);
        this.setInvulnerable(true);
        this.bossEvent.setProgress(0.0F);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public boolean isCastingSpell() {
        if (this.level().isClientSide) {
            return currentSpell != null;
        } else {
            return this.spellCastingTickCount > 0;
        }
    }

    public void setCastingSpell(@Nullable WandAbilityInstance currentSpell) {
        this.currentSpell = currentSpell;
        if (currentSpell != null) {
            this.setIsAttacking(true);
        }
    }

    public SoundEvent getCastingSound() {
        return SoundEvents.GHAST_SCREAM;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.EVOKER_AMBIENT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.EVOKER_HURT;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(this, 10,
                        state -> state.setAndContinue(
                                this.getInvulnerableTicks() > 0 ? DefaultAnimations.SPAWN : DefaultAnimations.IDLE)),
                new AnimationController<>(this, 10,
                        state -> state
                                .setAndContinue(this.isAttacking() ? DefaultAnimations.ATTACK_CAST : DefaultAnimations.IDLE)),
                DefaultAnimations.genericWalkController(this),
                DefaultAnimations.genericIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        if (currentSpell != null) {
            currentSpell.execute(this.level(), this, this.getMainHandItem(),
                    new WandAbilityInstance.WrappedVec3(this.getEyePosition()), 50);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    private ItemStack createBossWand() {
        HolderLookup.RegistryLookup<Enchantment> enchantRegistry = this.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        var wandItem = MOTMUtil.createWand(MOTMWandAbilities.LARGE_FIREBALL.get(), MOTMWandAbilities.HEAL_SELF.get());
        enchantRegistry.get(Enchantments.POWER).ifPresent(holder -> wandItem.enchant(holder, 1));
        enchantRegistry.get(Enchantments.QUICK_CHARGE).ifPresent(holder -> wandItem.enchant(holder, 3));

        return wandItem;
    }

    class WizardDoNothingGoal extends Goal {

        public WizardDoNothingGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return BossWizardEntity.this.getInvulnerableTicks() > 0;
        }
    }
}
