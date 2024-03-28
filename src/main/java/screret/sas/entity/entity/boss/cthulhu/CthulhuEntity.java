package screret.sas.entity.entity.boss.cthulhu;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import screret.sas.api.capability.cthulhu.CthulhuFightSavedData;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.boss.cthulhu.part.CthulhuPart;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class CthulhuEntity extends Mob implements Enemy, GeoEntity {
    private static final Predicate<LivingEntity> LIVING_ENTITY_SELECTOR = (mob) -> mob.getMobType() != MobType.ILLAGER && mob.attackable();
    private static final EntityDataAccessor<Boolean> IS_ATTACKING = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> INVULNERABLE_TICKS = SynchedEntityData.defineId(BossWizardEntity.class, EntityDataSerializers.INT);
    private static final int MAX_INVULNERABLE_TICKS = 75;

    private final CthulhuPart[] subEntities;
    public final CthulhuPart head;
    private final CthulhuPart neck;
    private final CthulhuPart body;
    private final CthulhuPart tail1;
    private final CthulhuPart tail2;
    private final CthulhuPart tail3;
    private final CthulhuPart wing1;
    private final CthulhuPart wing2;
    @Nullable
    public EndCrystal nearestCrystal;
    @Nullable
    private final CthulhuFight cthulhuFight;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CthulhuEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.head = new CthulhuPart(this, "head", 1.0F, 1.0F);
        this.neck = new CthulhuPart(this, "neck", 3.0F, 3.0F);
        this.body = new CthulhuPart(this, "body", 5.0F, 3.0F);
        this.tail1 = new CthulhuPart(this, "tail", 2.0F, 2.0F);
        this.tail2 = new CthulhuPart(this, "tail", 2.0F, 2.0F);
        this.tail3 = new CthulhuPart(this, "tail", 2.0F, 2.0F);
        this.wing1 = new CthulhuPart(this, "wing", 4.0F, 2.0F);
        this.wing2 = new CthulhuPart(this, "wing", 4.0F, 2.0F);
        this.subEntities = new CthulhuPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.wing1, this.wing2};
        this.setHealth(this.getMaxHealth());
        this.noCulling = true;
        if (pLevel instanceof ServerLevel serverLevel) {
            this.cthulhuFight = CthulhuFightSavedData.getOrCreate(serverLevel).getCurrentFight();
        } else {
            this.cthulhuFight = null;
        }

        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1); // Forge: Fix MC-158205: Make sure part ids are successors of parent mob id
    }

    @Override
    public void setId(int pId) {
        super.setId(pId);
        for (int i = 0; i < this.subEntities.length; i++) // Forge: Fix MC-158205: Set part ids to successors of parent mob id
            this.subEntities[i].setId(pId + i + 1);
    }

    public boolean hurt(CthulhuPart pPart, DamageSource pSource, float pDamage) {
        return false;
    }

    public int getInvulnerableTicks() {
        return this.entityData.get(INVULNERABLE_TICKS);
    }

    public void setInvulnerableTicks(int pInvulnerableTicks) {
        this.entityData.set(INVULNERABLE_TICKS, pInvulnerableTicks);
    }

    public boolean isAttacking() {
        return this.entityData.get(IS_ATTACKING);
    }

    public void setIsAttacking(boolean isAttacking) {
        this.entityData.set(IS_ATTACKING, isAttacking);
    }

    protected boolean reallyHurt(DamageSource pDamageSource, float pAmount) {
        return super.hurt(pDamageSource, pAmount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
