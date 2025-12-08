package dev.screret.motm;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Predicate;

public class MOTMUtil {

    private static final ResourceLocation TEMPLATE_LOCATION = ResourceLocation.fromNamespaceAndPath(MagicOfTheMind.MOD_ID, "");

    public static double randomInRange(RandomSource randomSource, double min, double max) {
        return (randomSource.nextDouble() * (max - min)) + min;
    }

    public static ResourceLocation id(String path) {
        return TEMPLATE_LOCATION.withPath(path);
    }

    public static BlockHitResult getHitResult(Level level, LivingEntity entity, ClipContext.Fluid fluidInteractionMode,
                                              double distance) {
        Vec3 eyePos = entity.getEyePosition(0);
        Vec3 viewVector = entity.getViewVector(0);
        Vec3 result = eyePos.add(viewVector.x * distance, viewVector.y * distance, viewVector.z * distance);
        return entity.level().clip(new ClipContext(eyePos, result, ClipContext.Block.OUTLINE, fluidInteractionMode, entity));
    }

    public static EntityHitResult getHitResult(Level level, LivingEntity entity, Predicate<Entity> filter, double distance) {
        Vec3 eyePos = entity.getEyePosition(0);
        Vec3 viewVector = entity.getViewVector(0);
        Vec3 result = eyePos.add(viewVector.x * distance, viewVector.y * distance, viewVector.z * distance);
        return ProjectileUtil.getEntityHitResult(entity, entity.getEyePosition(), result,
                AABB.ofSize(eyePos, distance, distance, distance), filter, distance);
    }

    public static void spawnParticlesInLine(Level level, Vec3 start, Vec3 end, ParticleOptions particle, int pointsPerLine,
                                            Vec3 randomDeviation, boolean alwaysRender) {
        double d = start.distanceTo(end) / pointsPerLine;
        for (int i = 0; i < pointsPerLine; i++) {
            Vec3 pos = new Vec3(start.x, start.y, start.z);
            Vec3 direction = end.subtract(start).normalize();
            Vec3 v = direction.multiply(i * d, i * d, i * d);

            pos = pos.add(v);
            if (level.isClientSide) {
                level.addParticle(particle, alwaysRender, pos.x, pos.y, pos.z, randomDeviation.x, randomDeviation.y,
                        randomDeviation.z);
                continue;
            }
            ((ServerLevel) level).sendParticles(particle, pos.x, pos.y, pos.z, 1, randomDeviation.x, randomDeviation.y,
                    randomDeviation.z, 0);
        }
    }
}
