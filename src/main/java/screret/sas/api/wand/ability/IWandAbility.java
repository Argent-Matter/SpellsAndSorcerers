package screret.sas.api.wand.ability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IWandAbility {

    InteractionResultHolder<ItemStack> execute(Level level, LivingEntity player, ItemStack stack, WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged);

    int getUseDuration();

    boolean isHoldable();

    boolean isChargeable();

    int getCooldownDuration();

    float getBaseDamagePerHit();

    float getDamagePerHit(ItemStack stack);

    ResourceLocation getKey();

    int getColor();

    WandAbilityInstance getBasicInstance();
}
