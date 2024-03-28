package screret.sas.ability.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.EnumSet;

public class HealSelfAbility extends WandAbility {
    public static final Codec<HealSelfAbility> CODEC = RecordCodecBuilder.create(instance -> WandAbility.codecStart(instance).apply(instance, HealSelfAbility::new));

    public HealSelfAbility() {
        super(10, 10, .25f, true, ParticleTypes.HAPPY_VILLAGER, 0xFF00ae2d);
    }

    public HealSelfAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color);
    }

    @Override
    public Codec<? extends WandAbility> codec() {
        return CODEC;
    }

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        user.heal(getDamagePerHit(stack));
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean isHoldable() {
        return true;
    }
}
