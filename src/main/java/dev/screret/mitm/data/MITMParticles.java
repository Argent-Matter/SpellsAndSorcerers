package dev.screret.mitm.data;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.mitm.MagicOfTheMind;

import java.util.function.Supplier;

public class MITMParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MagicOfTheMind.MODID);

    public static final Supplier<SimpleParticleType> EYE = PARTICLES.register("eye", () -> new SimpleParticleType(true));
}
