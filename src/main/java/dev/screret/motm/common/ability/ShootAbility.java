package dev.screret.motm.common.ability;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.ability.WandAbility;
import dev.screret.motm.api.ability.WandAbilityInstance;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ShootAbility extends WandAbility<ShootAbility> {

    // spotless:off
    private static final MapCodec<ShootAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> WandAbility.codecStart(instance).and(instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("distance").forGetter((ShootAbility val) -> val.distance),
            Vec3.CODEC.fieldOf("particle_deviation").forGetter((ShootAbility val) -> val.randomDeviation)
    )).apply(instance, ShootAbility::new));
    // spotless:on

    private final int distance;

    private final Vec3 randomDeviation;

    public ShootAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants,
                        ParticleOptions particle, int color, int distance, Vec3 randomDeviation) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color);
        this.distance = distance;
        this.randomDeviation = randomDeviation;
    }

    @Override
    public MapCodec<ShootAbility> codec() {
        return CODEC;
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack,
                                                      WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        if (!level.isClientSide) {
            var distanceSqr = distance * distance;
            var hitResult = MOTMUtil.getHitResult(level, user, (entity) -> entity != user, distanceSqr);

            var userPos = user instanceof Player ? user.getEyePosition().subtract(0.0, 0.35, 0.0) : user.getEyePosition();
            if (hitResult != null) {
                if (particle != null)
                    MOTMUtil.spawnParticlesInLine(level, userPos, hitResult.getLocation(), particle,
                            ((int) userPos.distanceTo(hitResult.getLocation()) * 4), randomDeviation, false);
                currentPosition.real = hitResult.getLocation();
                return InteractionResultHolder.pass(stack);
            }

            var blockHit = MOTMUtil.getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);
            currentPosition.real = blockHit.getLocation();
            if (particle != null)
                MOTMUtil.spawnParticlesInLine(level, userPos, blockHit.getLocation(), particle,
                        (int) (user.position().distanceTo(blockHit.getLocation()) * 4), randomDeviation, false);
        }
        return InteractionResultHolder.pass(stack);
    }
}
