package dev.screret.mitm.common.ability;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import dev.screret.mitm.MITMUtil;

public class LargeFireballAbility extends ProjectileAbility<LargeFireballAbility> {
    private static final MapCodec<LargeFireballAbility> CODEC = RecordCodecBuilder.mapCodec(instance ->
            ProjectileAbility.projectileCodecStart(instance)
                    .and(ExtraCodecs.POSITIVE_INT.fieldOf("explosion_power").forGetter((LargeFireballAbility val) -> val.explosionPower))
                    .apply(instance, LargeFireballAbility::new));
    private final int explosionPower;

    public LargeFireballAbility() {
        super(0, 20, 0.5f, true, 0xFFff6200, 512);
        this.explosionPower = 1;
    }

    public LargeFireballAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particleOptions, int color, int distance, int explosionPower) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particleOptions, color, distance);
        this.explosionPower = explosionPower;
    }

    @Override
    public MapCodec<LargeFireballAbility> codec() {
        return CODEC;
    }

    @Override
    public Projectile spawnProjectile(Level level, LivingEntity user, ItemStack usedItem, int timeCharged) {
        //int explosionPower = (int) (getDamagePerHit(usedItem) * timeCharged / 8);

        int distanceSqr = distance * distance;
        BlockHitResult hitResult = MITMUtil.getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);

        Vec3 userPos = user.getEyePosition().subtract(0.0, 0.35, 0.0);
        double dirX = hitResult.getLocation().x - user.getX();
        double dirY = hitResult.getLocation().y - userPos.y;
        double dirZ = hitResult.getLocation().z - user.getZ();

        double distanceToEndSqrtHalf = Math.sqrt(userPos.distanceTo(hitResult.getLocation())) * 0.5D;

        LargeFireball result = new LargeFireball(level, user,
                new Vec3(
                        level.getRandom().triangle(dirX, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtHalf),
                        dirY,
                        level.getRandom().triangle(dirZ, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtHalf)),
                explosionPower);
        result.moveTo(userPos);
        return result;
    }
}
