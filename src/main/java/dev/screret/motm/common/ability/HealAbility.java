package dev.screret.motm.common.ability;

import dev.screret.motm.api.ability.WandAbility;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.EnumSet;

public class HealAbility extends SubAbility<HealAbility> {

    private static final MapCodec<HealAbility> CODEC = RecordCodecBuilder
            .mapCodec(instance -> WandAbility.codecStart(instance).apply(instance, HealAbility::new));

    public HealAbility() {
        super(20, 40, .25f, true, ParticleTypes.HAPPY_VILLAGER, 0xFF00ae2d, EnumSet.of(HitFlags.ENTITY));
    }

    public HealAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle,
                       int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color, EnumSet.of(HitFlags.ENTITY));
    }

    @Override
    public MapCodec<HealAbility> codec() {
        return CODEC;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity target, float timeCharged) {
        DamageSource source = user.damageSources().magic();
        target.heal(getDamagePerHit(usedItem, user, source));
        return true;
    }

    @Override
    public boolean isHoldable() {
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        return false;
    }
}
