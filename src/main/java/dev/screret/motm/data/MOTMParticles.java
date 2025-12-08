package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MOTMParticles {

    // spotless:off
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MagicOfTheMind.MOD_ID);

    public static final Supplier<SimpleParticleType> EYE = PARTICLES.register("eye", () -> new SimpleParticleType(true));

    // spotless:on
}
