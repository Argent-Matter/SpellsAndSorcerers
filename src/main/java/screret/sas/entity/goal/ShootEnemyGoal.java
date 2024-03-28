package screret.sas.entity.goal;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.WizardEntity;

import javax.annotation.Nullable;
import java.util.EnumSet;

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

    public ShootEnemyGoal(BossWizardEntity pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
        this(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackInterval, pAttackRadius);
    }

    public ShootEnemyGoal(BossWizardEntity pRangedAttackMob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax, float pAttackRadius) {
        this.mob = pRangedAttackMob;
        this.speedModifier = pSpeedModifier;
        this.attackIntervalMin = pAttackIntervalMin;
        this.attackIntervalMax = pAttackIntervalMax;
        this.attackRadius = pAttackRadius;
        this.attackRadiusSqr = pAttackRadius * pAttackRadius;
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
        this.mob.setCastingSpell(BossWizardEntity.DUMMY_SPELL);
        this.mob.setIsAttacking(false);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void tick() {
        double d0 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
        boolean flag = this.mob.getSensing().hasLineOfSight(this.target);
        if (flag) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (!(d0 > (double) this.attackRadiusSqr) && this.seeTime >= 5) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.target, this.speedModifier);
        }

        this.mob.getLookControl().setLookAt(this.target, this.mob.getMaxHeadYRot(), this.mob.getMaxHeadXRot());
        if (--this.attackTime == 0) {
            if (!flag) {
                return;
            }

            float f = (float) Math.sqrt(d0) / this.attackRadius;
            float f1 = Mth.clamp(f, 0.1F, 1.0F);
            if (this.target != null) {
                this.mob.setIsAttacking(true);
                this.mob.playSound(this.mob.getCastingSound());
                this.mob.setCastingSpell(getSpell(this.mob.getRandom()));
                this.mob.performRangedAttack(this.target, f1);
            }

            this.attackTime = Mth.floor(f * (float) (this.attackIntervalMax - this.attackIntervalMin) + (float) this.attackIntervalMin);
        } else if (this.attackTime < 0) {
            this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(d0) / (double) this.attackRadius, this.attackIntervalMin, this.attackIntervalMax));
        }
    }

    protected WandAbilityInstance getSpell(RandomSource random) {
        if (WizardEntity.possibleWands == null) {
            WizardEntity.possibleWands = Util.CUSTOM_WANDS.values().stream().toList();
        }
        return Util.getMainAbilityFromStack(WizardEntity.possibleWands.get(random.nextInt(WizardEntity.possibleWands.size() - 1))).get();
    }
}