package dev.screret.motm;

import dev.screret.motm.api.ability.WandAbility;
import dev.screret.motm.api.ability.WandAbilityInstance;
import dev.screret.motm.api.registry.MOTMRegistries;
import dev.screret.motm.common.item.component.WandComponent;
import dev.screret.motm.data.MOTMDataComponents;
import dev.screret.motm.data.MOTMItems;
import dev.screret.motm.data.MOTMWandAbilities;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

public class MOTMUtil {

    public static final Map<ResourceLocation, ItemStack> CUSTOM_WANDS = Maps.newHashMap();
    public static final Map<ResourceLocation, ItemStack> CUSTOM_WAND_CORES = Maps.newHashMap();

    private static final ResourceLocation TEMPLATE_LOCATION = ResourceLocation.fromNamespaceAndPath(MagicOfTheMind.MODID, "");

    public static void generateWandItems() {
        if (!CUSTOM_WANDS.isEmpty()) {
            return;
        }

        addWand(new WandAbilityInstance(MOTMWandAbilities.SHOOT_RAY.get(),
                new WandAbilityInstance(MOTMWandAbilities.DAMAGE.get())), null);
        addWand(new WandAbilityInstance(MOTMWandAbilities.SHOOT_HOLD_DOWN.get(),
                new WandAbilityInstance(MOTMWandAbilities.HEAL.get())),
                new WandAbilityInstance(MOTMWandAbilities.HEAL_SELF.get()));
        addWand(new WandAbilityInstance(MOTMWandAbilities.SHOOT_ANGRY_RAY.get(),
                new WandAbilityInstance(MOTMWandAbilities.EXPLODE.get())), null);

        addWand(MOTMWandAbilities.SMALL_FIREBALL.get(), null);
        addWand(MOTMWandAbilities.LARGE_FIREBALL.get(), null);
        addWand(new WandAbilityInstance(MOTMWandAbilities.SHOOT_LIGHTNING.get(),
                new WandAbilityInstance(MOTMWandAbilities.LIGHTNING.get())), null);

        MOTMRegistries.WAND_ABILITIES.holders().forEach(ability -> {
            addWandCore(ability.value());
        });
    }

    public static ItemStack addWand(WandAbility<?> primary, @Nullable WandAbility<?> secondary) {
        return CUSTOM_WANDS.put(primary.getKey(), createWand(primary, secondary));
    }

    public static ItemStack addWand(WandAbilityInstance main, @Nullable WandAbilityInstance crouch) {
        var childestAbility = main;
        while (!childestAbility.getChildren().isEmpty()) {
            childestAbility = childestAbility.getChildren().getFirst();
        }
        return CUSTOM_WANDS.put(childestAbility.getId(), createWand(MOTMItems.WAND.get(), main, crouch));
    }

    public static ItemStack addWandCore(WandAbility<?> ability) {
        var coreStack = new ItemStack(MOTMItems.WAND_CORE.get());
        coreStack.set(MOTMDataComponents.WAND_CORE, new WandAbilityInstance(ability));

        return CUSTOM_WAND_CORES.put(ability.getKey(), coreStack);
    }

    public static Optional<WandAbilityInstance> getMainAbilityFromStack(ItemStack stack) {
        if (stack.is(MOTMItems.WAND_CORE)) {
            return Optional.ofNullable(stack.get(MOTMDataComponents.WAND_CORE));
        } else {
            return Optional.ofNullable(stack.get(MOTMDataComponents.WAND)).map(WandComponent::primary);
        }
    }

    public static ItemStack createWand(WandAbility<?> primary, @Nullable WandAbility<?> secondary) {
        WandAbilityInstance primaryInstance = new WandAbilityInstance(primary);
        WandAbilityInstance secondaryInstance = secondary != null ? new WandAbilityInstance(secondary) : null;
        return createWand(MOTMItems.WAND.get(), primaryInstance, secondaryInstance);
    }

    public static ItemStack createWand(Item item, WandAbilityInstance primary, @Nullable WandAbilityInstance secondary) {
        ItemStack stack = new ItemStack(item);
        stack.set(MOTMDataComponents.WAND, new WandComponent(primary, Optional.ofNullable(secondary), false));
        return stack;
    }

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
