package screret.sas.client.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;

import java.util.function.Supplier;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SpellsAndSorcerers.MODID);

    public static final Supplier<SimpleParticleType> EYE = PARTICLES.register("eye", () -> new SimpleParticleType(true));
}
