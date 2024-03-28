package screret.sas.ability.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import screret.sas.api.wand.ability.WandAbility;

import java.util.EnumSet;

public class DamageAbility extends SubAbility {

    public static final Codec<DamageAbility> CODEC = RecordCodecBuilder.create(instance -> WandAbility.codecStart(instance).apply(instance, DamageAbility::new));

    public DamageAbility() {
        super(0, 10, 3, true, ParticleTypes.SOUL_FIRE_FLAME, 0x54cbcfFF, EnumSet.of(HitFlags.ENTITY));
    }

    public DamageAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color, EnumSet.of(HitFlags.ENTITY));
    }

    @Override
    public Codec<? extends WandAbility> codec() {
        return CODEC;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity hitEnt, float timeCharged) {
        hitEnt.hurt(user.damageSources().indirectMagic(user, user), getDamagePerHit(usedItem));
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        return false;
    }
}
