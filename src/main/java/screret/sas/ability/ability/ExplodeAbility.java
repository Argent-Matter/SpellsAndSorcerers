package screret.sas.ability.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import screret.sas.api.wand.ability.WandAbility;

import java.util.EnumSet;

public class ExplodeAbility extends SubAbility {

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0xFFAA0000).toVector3f()), 2.0F);
    public static final Vec3 RANDOM_DEVIATION = new Vec3(0.125D, 0.125D, 0.125D);

    public static final Codec<ExplodeAbility> CODEC = RecordCodecBuilder.create(instance -> WandAbility.codecStart(instance).apply(instance, ExplodeAbility::new));

    public ExplodeAbility() {
        super(100, 60, 1, true, ExplodeAbility.PARTICLE, 0xAA0000, EnumSet.of(HitFlags.BLOCK, HitFlags.NONE));
    }

    public ExplodeAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color, EnumSet.of(HitFlags.BLOCK, HitFlags.NONE));
    }

    @Override
    public Codec<? extends WandAbility> codec() {
        return CODEC;
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity hitEnt, float timeCharged) {
        var explosionPower = getDamagePerHit(usedItem) * timeCharged / 8;
        user.level().explode(user, hitEnt.getX(), hitEnt.getY(), hitEnt.getZ(), explosionPower, Level.ExplosionInteraction.MOB);
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        var explosionPower = getDamagePerHit(usedItem) * timeCharged / 8;
        user.level().explode(user, hitPoint.x, hitPoint.y, hitPoint.z, explosionPower, Level.ExplosionInteraction.MOB);
        return true;
    }
}
