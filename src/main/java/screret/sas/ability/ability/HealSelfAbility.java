package screret.sas.ability.ability;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

public class HealSelfAbility extends WandAbility {
    public HealSelfAbility() {
        super(10, 10, .25f, true, ParticleTypes.HAPPY_VILLAGER, 0xFF00ae2d);
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
