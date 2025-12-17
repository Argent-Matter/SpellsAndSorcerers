package dev.screret.motm.common.entity;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public class Elderling extends AbstractVillager implements GeoEntity, SmartBrainOwner<Elderling> {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Elderling(EntityType<? extends Elderling> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.8)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_SWING),
                DefaultAnimations.genericWalkIdleController(this),
                DefaultAnimations.genericDeathController(this));
    }

    // spotless:off
    @Override
    public List<ExtendedSensor<Elderling>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),   // This tracks nearby entities
                new HurtBySensor<>()                // This tracks the last damage source and attacker
        );
    }

    @Override
    public BrainActivityGroup<Elderling> getCoreTasks() {
        // These are the tasks that run all the time (usually)
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),       // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>()    // Walk towards the current walk target
        );
    }

    @Override
    public BrainActivityGroup<Elderling> getIdleTasks() {
        // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>( // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>(),            // Set the attack target and walk target based on nearby entities
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                // Run a random task from the below options
                        new SetRandomWalkTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))) // Do nothing for 1.5->3 seconds
        );
    }

    @Override
    public BrainActivityGroup<Elderling> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),         // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>(),    // Set the walk target to the attack target
                new AnimatableMeleeAttack<>(0)          // Melee attack the target if close enough
        );
    }
    // spotless:on

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("elderlingBrain");
        tickBrain(this);
        this.level().getProfiler().pop();
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        // TODO smarter XP rewards
        // this this copied from wandering traders.
        if (offer.shouldRewardExp()) {
            int xp = this.random.nextInt(4) + 3;
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), xp));
        }
    }

    @Override
    protected void updateTrades() {
        // TODO add trades (?)
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // TODO implement children
        return null;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }
}
