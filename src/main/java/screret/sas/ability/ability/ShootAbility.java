package screret.sas.ability.ability;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

public class ShootAbility extends WandAbility {

    private final int distance;

    private final Vec3 randomDeviation;

    public ShootAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, int distance, ParticleOptions particle, Vec3 randomDeviation, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color);
        this.distance = distance;
        this.randomDeviation = randomDeviation;
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        if (!level.isClientSide) {
            var distanceSqr = distance * distance;
            var hitResult = Util.getHitResult(level, user, (entity) -> entity != user, distanceSqr);

            var userPos = user instanceof Player ? user.getEyePosition().subtract(0.0, 0.35, 0.0) : user.getEyePosition();
            if (hitResult != null) {
                if (particle != null)
                    Util.spawnParticlesInLine(level, userPos, hitResult.getLocation(), particle, ((int) userPos.distanceTo(hitResult.getLocation()) * 4), randomDeviation, false);
                currentPosition.real = hitResult.getLocation();
                return InteractionResultHolder.pass(stack);
            }

            var blockHit = Util.getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);
            currentPosition.real = blockHit.getLocation();
            if (particle != null)
                Util.spawnParticlesInLine(level, userPos, blockHit.getLocation(), particle, (int) (user.position().distanceTo(blockHit.getLocation()) * 4), randomDeviation, false);
        }
        return InteractionResultHolder.pass(stack);
    }
}