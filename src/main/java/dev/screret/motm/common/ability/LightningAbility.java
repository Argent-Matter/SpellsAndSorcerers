package dev.screret.motm.common.ability;

import dev.screret.motm.api.ability.WandAbility;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.EnumSet;

public class LightningAbility extends SubAbility<LightningAbility> {

    private static final MapCodec<LightningAbility> CODEC = RecordCodecBuilder
            .mapCodec(instance -> WandAbility.codecStart(instance).apply(instance, LightningAbility::new));

    public LightningAbility() {
        super(0, 25, 2, true, ParticleTypes.ELECTRIC_SPARK, 0xFFAAAAAA,
                EnumSet.of(HitFlags.ENTITY, HitFlags.BLOCK, HitFlags.NONE));
    }

    public LightningAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants,
                            ParticleOptions particle, int color) {
        super(useDuration, cooldownDuration, damagePerHit, applyEnchants, particle, color,
                EnumSet.of(HitFlags.ENTITY, HitFlags.BLOCK, HitFlags.NONE));
    }

    @Override
    public MapCodec<LightningAbility> codec() {
        return CODEC;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, LivingEntity target, float timeCharged) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(target.level());
        lightning.moveTo(target.getX(), target.getY(), target.getZ());
        lightning.setVisualOnly(false);
        lightning.setCause(user instanceof ServerPlayer player ? player : null);
        lightning.setDamage(this.getDamagePerHit(usedItem, user, user.damageSources().lightningBolt()));

        target.level().addFreshEntity(lightning);

        return true;
    }

    @Override
    public boolean doHit(ItemStack usedItem, LivingEntity user, Vec3 hitPoint, float timeCharged) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(user.level());
        lightning.moveTo(hitPoint.x, hitPoint.y, hitPoint.z);
        lightning.setVisualOnly(false);
        lightning.setCause(user instanceof ServerPlayer player ? player : null);
        lightning.setDamage(this.getDamagePerHit(usedItem, user, user.damageSources().lightningBolt()));
        user.level().addFreshEntity(lightning);
        return true;
    }
}
