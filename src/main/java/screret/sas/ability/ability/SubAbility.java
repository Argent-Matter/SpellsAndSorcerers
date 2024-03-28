package screret.sas.ability.ability;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.EnumSet;
import java.util.List;

public abstract class SubAbility extends WandAbility {

    private static final EntityTypeTest<Entity, LivingEntity> ANY_LIVING_ENTITY_TYPE = new EntityTypeTest<>() {
        public LivingEntity tryCast(Entity entity) {
            return entity instanceof LivingEntity living ? living : null;
        }

        public Class<LivingEntity> getBaseClass() {
            return LivingEntity.class;
        }
    };

    private final EnumSet<HitFlags> hitFlags;

    public SubAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle, EnumSet<HitFlags> hitFlags, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color);
        this.hitFlags = hitFlags;
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        if (level.isClientSide)
            return InteractionResultHolder.pass(stack);

        if (hitFlags.contains(HitFlags.NONE) || hitFlags.contains(HitFlags.BLOCK)) {
            if (doHit(stack, user, currentPosition.real, timeCharged)) {
                return InteractionResultHolder.pass(stack);
            }
        }
        if (hitFlags.contains(HitFlags.ENTITY)) {
            AABB bounds = AABB.ofSize(currentPosition.real, 0.01, 0.01, 0.01);
            List<LivingEntity> allHitPossibilities = level.getEntities(SubAbility.ANY_LIVING_ENTITY_TYPE, bounds, entity -> entity != user);
            allHitPossibilities.sort((thisPart, next) -> (int) Math.round(next.position().distanceTo(currentPosition.real) - thisPart.position().distanceTo(currentPosition.real)));
            if (allHitPossibilities.size() > 0) {
                if (doHit(stack, user, allHitPossibilities.get(0), timeCharged)) {
                    return InteractionResultHolder.pass(stack);
                }
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    public abstract boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity hitEnt, float timeCharged);

    public abstract boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged);

    public enum HitFlags {
        NONE,
        ENTITY,
        BLOCK,
    }
}
