package screret.sas.ability.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import screret.sas.api.wand.ability.WandAbility;

import java.util.EnumSet;

public class HealAbility extends SubAbility {

    public HealAbility() {
        super(20, 40, .25f, true, ParticleTypes.HAPPY_VILLAGER, EnumSet.of(HitFlags.ENTITY), 0xFF00ae2d);
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
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged)
    {
        return false;
    }

}
