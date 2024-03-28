package screret.sas.ability.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbility;

public class LargeFireballAbility extends ProjectileAbility {
    public static final Codec<LargeFireballAbility> CODEC = RecordCodecBuilder.create(instance ->
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
    public Codec<? extends WandAbility> codec() {
        return CODEC;
    }

    @Override
    public Projectile spawnProjectile(Level level, LivingEntity user, ItemStack usedItem, int timeCharged) {
        //int explosionPower = (int) (getDamagePerHit(usedItem) * timeCharged / 8);

        var distanceSqr = distance * distance;
        var hitResult = Util.getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);

        var userPos = user.getEyePosition().subtract(0.0, 0.35, 0.0);
        var dirX = hitResult.getLocation().x - user.getX();
        var dirY = hitResult.getLocation().y - userPos.y;
        var dirZ = hitResult.getLocation().z - user.getZ();

        var distanceToEndSqrtHalf = Math.sqrt(userPos.distanceTo(hitResult.getLocation())) * 0.5D;

        var result = new LargeFireball(level, user, level.getRandom().triangle(dirX, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtHalf), dirY, level.getRandom().triangle(dirZ, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtHalf), explosionPower);
        result.moveTo(userPos);
        return result;
    }
}
