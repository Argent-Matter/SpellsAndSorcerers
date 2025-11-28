package dev.screret.mitm.common.ability;

import dev.screret.mitm.api.ability.WandAbility;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.EnumSet;

public class DamageAbility extends SubAbility<DamageAbility> {

    private static final MapCodec<DamageAbility> CODEC = RecordCodecBuilder
            .mapCodec(instance -> WandAbility.codecStart(instance).apply(instance, DamageAbility::new));

    public DamageAbility() {
        super(0, 10, 3, true, ParticleTypes.SOUL_FIRE_FLAME, 0x54cbcfFF, EnumSet.of(HitFlags.ENTITY));
    }

    public DamageAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants,
                         ParticleOptions particle, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color, EnumSet.of(HitFlags.ENTITY));
    }

    @Override
    public MapCodec<DamageAbility> codec() {
        return CODEC;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity target, float timeCharged) {
        DamageSource source = user.damageSources().indirectMagic(user, user);
        target.hurt(source, getDamagePerHit(usedItem, user, source));
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        return false;
    }
}
