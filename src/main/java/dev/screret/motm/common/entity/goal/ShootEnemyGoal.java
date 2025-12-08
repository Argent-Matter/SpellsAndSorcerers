package dev.screret.motm.common.entity.goal;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.ability.WandAbilityInstance;
import dev.screret.motm.common.entity.BossWizardEntity;
import dev.screret.motm.common.entity.WizardEntity;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class ShootEnemyGoal extends Goal {

    private final BossWizardEntity mob;
    @Nullable
    private LivingEntity target;
    private int attackTime = -1;
    private final double speedModifier;
    private int seeTime;
    private final int attackIntervalMin;
    private final int attackIntervalMax;
    private final float attackRadius;
    private final float attackRadiusSqr;

    public ShootEnemyGoal(BossWizardEntity rangedAttackMob, double speedModifier, int attackInterval, float attackRadius) {
        this(rangedAttackMob, speedModifier, attackInterval, attackInterval, attackRadius);
    }

    public ShootEnemyGoal(BossWizardEntity rangedAttackMob, double speedModifier, int attackIntervalMin, int attackIntervalMax,
                          float attackRadius) {
        this.mob = rangedAttackMob;
        this.speedModifier = speedModifier;
        this.attackIntervalMin = attackIntervalMin;
        this.attackIntervalMax = attackIntervalMax;
        this.attackRadius = attackRadius;
        this.attackRadiusSqr = attackRadius * attackRadius;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            this.target = livingentity;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean canContinueToUse() {
        return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void stop() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
        this.mob.setCastingSpell(null);
        this.mob.setIsAttacking(false);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        double distance = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean canSee = this.mob.getSensing().hasLineOfSight(this.target);
        if (canSee) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (!(distance > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier);
        }

        this.mob.getLookControl().setLookAt(this.target, this.mob.getMaxHeadYRot(), this.mob.getMaxHeadXRot());
        if (--this.attackTime == 0) {
            if (!canSee) {
                return;
            }

            float distanceRatio = (float) Math.sqrt(distance) / this.attackRadius;
            float clampedRatio = Mth.clamp(distanceRatio, 0.1F, 1.0F);
            if (this.target != null) {
                this.mob.setIsAttacking(true);
                this.mob.playSound(this.mob.getCastingSound());
                this.mob.setCastingSpell(getSpell(this.mob.getRandom()));
                this.mob.performRangedAttack(this.target, clampedRatio);
            }

            this.attackTime = Mth
                    .floor(distanceRatio * (float) (this.attackIntervalMax - this.attackIntervalMin) +
                            (float) this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth
                    .floor(Mth.lerp(Math.sqrt(distance) / (double) this.attackRadius, this.attackIntervalMin,
                            this.attackIntervalMax));
        }
    }

    protected @Nullable WandAbilityInstance getSpell(RandomSource random) {
        if (WizardEntity.possibleWands == null) {
            WizardEntity.possibleWands = List.copyOf(MOTMUtil.CUSTOM_WANDS.values());
        }
        return MOTMUtil.getMainAbilityFromStack(WizardEntity.possibleWands
                .get(random.nextInt(WizardEntity.possibleWands.size() - 1)))
                .orElse(null);
    }
}
