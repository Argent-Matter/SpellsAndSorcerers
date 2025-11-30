package dev.screret.motm.common.ability;

import dev.screret.motm.MOTMUtil;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SmallFireballAbility extends ProjectileAbility<SmallFireballAbility> {

    private static final MapCodec<SmallFireballAbility> CODEC = RecordCodecBuilder
            .mapCodec(instance -> ProjectileAbility.projectileCodecStart(instance).apply(instance, SmallFireballAbility::new));

    public SmallFireballAbility() {
        super(0, 10, 0, true, 0xFFffa500, 512);
    }

    public SmallFireballAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants,
                                ParticleOptions particle, int color, int distance) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color, distance);
    }

    @Override
    public MapCodec<SmallFireballAbility> codec() {
        return CODEC;
    }

    @Override
    public Projectile spawnProjectile(Level level, LivingEntity user, ItemStack usedItem, int timeCharged) {
        var distanceSqr = distance * distance;
        var hitResult = MOTMUtil.getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);

        var userPos = user.getEyePosition().subtract(0.0, 0.35, 0.0);
        var dirX = hitResult.getLocation().x - user.getX();
        var dirY = hitResult.getLocation().y - userPos.y;
        var dirZ = hitResult.getLocation().z - user.getZ();

        var distanceToEndSqrtHalf = Math.sqrt(userPos.distanceTo(hitResult.getLocation())) * 0.25D;

        var result = new SmallFireball(level, user,
                new Vec3(
                        level.getRandom().triangle(dirX, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtHalf),
                        dirY,
                        level.getRandom().triangle(dirZ, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtHalf)));
        result.moveTo(userPos);
        return result;
    }
}
