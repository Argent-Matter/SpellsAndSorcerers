package screret.sas.client.particle.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class EyeParticle extends SimpleAnimatedParticle {
    protected EyeParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites,0);
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

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new EyeParticle(pLevel, pX, pY, pZ, this.sprites);
        }
    }
}
