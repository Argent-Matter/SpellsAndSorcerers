package dev.screret.mitm.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class EyeParticle extends SimpleAnimatedParticle {

    protected EyeParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites, 0);
        this.quadSize = 0.75F;
        this.lifetime = 20;
        this.hasPhysics = false;
        this.setSpriteFromAge(this.sprites);
    }

    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.setSpriteFromAge(this.sprites);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed,
                                       double ySpeed, double zSpeed) {
            return new EyeParticle(level, x, y, z, this.sprites);
        }
    }
}
