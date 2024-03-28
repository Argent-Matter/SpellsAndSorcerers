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

public class HealAbility extends SubAbility {
    public static final Codec<HealAbility> CODEC = RecordCodecBuilder.create(instance -> WandAbility.codecStart(instance).apply(instance, HealAbility::new));

    public HealAbility() {
        super(20, 40, .25f, true, ParticleTypes.HAPPY_VILLAGER, 0xFF00ae2d, EnumSet.of(HitFlags.ENTITY));
    }

    public HealAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color, EnumSet.of(HitFlags.ENTITY));
    }

    @Override
    public Codec<? extends WandAbility> codec() {
        return CODEC;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity hitEnt, float timeCharged) {
        hitEnt.heal(getDamagePerHit(usedItem));
        return true;
    }

    @Override
    public boolean isHoldable() {
        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        return false;
    }

}
