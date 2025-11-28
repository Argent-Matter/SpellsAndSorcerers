package dev.screret.mitm.common.ability;

import dev.screret.mitm.api.ability.WandAbility;

import net.minecraft.core.particles.ParticleOptions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoopAbility extends WandAbility<NoopAbility> {

    private static final MapCodec<NoopAbility> CODEC = RecordCodecBuilder
            .mapCodec(instance -> codecStart(instance).apply(instance, NoopAbility::new));

    public NoopAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle,
                       int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color);
    }

    @Override
    public MapCodec<NoopAbility> codec() {
        return CODEC;
    }
}
