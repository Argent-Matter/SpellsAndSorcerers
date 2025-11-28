package dev.screret.mitm.common.ability;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import dev.screret.mitm.api.ability.WandAbility;

import java.util.EnumSet;

public class ExplodeAbility extends SubAbility<ExplodeAbility> {

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0xFFAA0000).toVector3f()), 2.0F);
    public static final Vec3 RANDOM_DEVIATION = new Vec3(0.125D, 0.125D, 0.125D);

    private static final MapCodec<ExplodeAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> WandAbility.codecStart(instance).apply(instance, ExplodeAbility::new));

    public ExplodeAbility() {
        super(100, 60, 1, true, ExplodeAbility.PARTICLE, 0xAA0000, EnumSet.of(HitFlags.BLOCK, HitFlags.NONE));
    }

    public ExplodeAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color, EnumSet.of(HitFlags.BLOCK, HitFlags.NONE));
    }

    @Override
    public MapCodec<ExplodeAbility> codec() {
        return CODEC;
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity target, float timeCharged) {
        DamageSource source = Explosion.getDefaultDamageSource(user.level(), user);

        var explosionPower = getDamagePerHit(usedItem, user, source) * timeCharged / 8;
        user.level().explode(user, source, null, target.position(),
                explosionPower, false, Level.ExplosionInteraction.MOB);
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        DamageSource source = Explosion.getDefaultDamageSource(user.level(), user);

        var explosionPower = getDamagePerHit(usedItem, user, source) * timeCharged / 8;
        user.level().explode(user, source, null, hitPoint,
                explosionPower, false, Level.ExplosionInteraction.MOB);
        return true;
    }
}
