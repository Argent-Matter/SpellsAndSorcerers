package screret.sas.ability.ability;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

public abstract class ProjectileAbility extends WandAbility {

    protected final int distance;

    public ProjectileAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle, int color, int distance) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color);
        //oldParticle = new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.BARRIER.defaultBlockState())
        this.distance = distance;
    }

    public ProjectileAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, int color, int distance) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, new DustParticleOptions(new Vector3f(Vec3.fromRGB24(color).toVector3f()), 2.0F), color);
        //oldParticle = new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.BARRIER.defaultBlockState())
        this.distance = distance;
    }

    public static <W extends ProjectileAbility> Products.P7<RecordCodecBuilder.Mu<W>, Integer, Integer, Float, Boolean, ParticleOptions, Integer, Integer> projectileCodecStart(RecordCodecBuilder.Instance<W> instance) {
        return WandAbility.codecStart(instance)
                .and(ExtraCodecs.POSITIVE_INT.fieldOf("distance").forGetter((W val) -> val.distance));
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        if (!level.isClientSide) {
            level.addFreshEntity(spawnProjectile(level, user, stack, timeCharged));
            return InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    public abstract Projectile spawnProjectile(Level level, LivingEntity user, ItemStack usedItem, int timeCharged);
}
