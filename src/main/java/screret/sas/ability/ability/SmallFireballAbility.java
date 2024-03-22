package screret.sas.ability.ability;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import screret.sas.Util;

public class SmallFireballAbility extends ProjectileAbility {
    public SmallFireballAbility() {
        super(0, 10, 0, true, 512, 0xFFffa500);
    }

    @Override
    public Projectile spawnProjectile(Level level, LivingEntity user, ItemStack usedItem, int timeCharged) {
        var distanceSqr = distance * distance;
        var hitResult = Util.getHitResult(level, user, ClipContext.Fluid.NONE, distanceSqr);

        var userPos = user.getEyePosition().subtract(0.0, 0.35, 0.0);
        var dirX = hitResult.getLocation().x - user.getX();
        var dirY = hitResult.getLocation().y - userPos.y;
        var dirZ = hitResult.getLocation().z - user.getZ();

        var distanceToEndSqrtHalf = Math.sqrt(userPos.distanceTo(hitResult.getLocation())) * 0.25D;

        var result = new SmallFireball(level, user, level.getRandom().triangle(dirX, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtHalf), dirY, level.getRandom().triangle(dirZ, RandomSource.GAUSSIAN_SPREAD_FACTOR * distanceToEndSqrtHalf));
        result.moveTo(userPos);
        return result;
    }
}
