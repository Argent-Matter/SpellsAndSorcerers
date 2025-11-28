package dev.screret.mitm.common.ability;

import dev.screret.mitm.api.ability.WandAbility;
import dev.screret.mitm.api.ability.WandAbilityInstance;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class HealSelfAbility extends WandAbility<HealSelfAbility> {

    private static final MapCodec<HealSelfAbility> CODEC = RecordCodecBuilder
            .mapCodec(instance -> WandAbility.codecStart(instance).apply(instance, HealSelfAbility::new));

    public HealSelfAbility() {
        super(10, 10, .25f, true, ParticleTypes.HAPPY_VILLAGER, 0xFF00ae2d);
    }

    public HealSelfAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants,
                           ParticleOptions particle, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color);
    }

    @Override
    public MapCodec<HealSelfAbility> codec() {
        return CODEC;
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack,
                                                      WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        DamageSource source = user.damageSources().magic();
        user.heal(getDamagePerHit(stack, user, source));
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean isHoldable() {
        return true;
    }
}
