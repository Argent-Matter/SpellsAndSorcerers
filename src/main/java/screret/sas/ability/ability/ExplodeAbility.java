package screret.sas.ability.ability;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import java.util.EnumSet;

public class ExplodeAbility extends SubAbility {

    public static final DustParticleOptions PARTICLE = new DustParticleOptions(new Vector3f(Vec3.fromRGB24(0xFFAA0000).toVector3f()), 2.0F);
    public static final Vec3 RANDOM_DEVIATION = new Vec3(0.125D, 0.125D, 0.125D);


    public ExplodeAbility() {
        super(100, 60, 1, true, ExplodeAbility.PARTICLE, EnumSet.of(HitFlags.BLOCK, HitFlags.NONE), 0xAA0000);
    }

    @Override
    public boolean isChargeable() {
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity hitEnt, float timeCharged) {
        var explosionPower = getDamagePerHit(usedItem) * timeCharged / 8;
        user.level.explode(user, hitEnt.getX(), hitEnt.getY(), hitEnt.getZ(), explosionPower, Level.ExplosionInteraction.MOB);
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        var explosionPower = getDamagePerHit(usedItem) * timeCharged / 8;
        user.level.explode(user, hitPoint.x, hitPoint.y, hitPoint.z, explosionPower, Level.ExplosionInteraction.MOB);
        return true;
    }
}
