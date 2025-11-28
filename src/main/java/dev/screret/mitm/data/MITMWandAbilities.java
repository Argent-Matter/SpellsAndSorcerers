package dev.screret.mitm.data;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.api.registry.MITMRegistries;
import dev.screret.mitm.common.ability.*;
import dev.screret.mitm.api.ability.WandAbility;


public class MITMWandAbilities {
    public static final DeferredRegister<WandAbility<?>> WAND_ABILITIES = DeferredRegister.create(MITMRegistries.WAND_ABILITIES, MagicOfTheMind.MODID);
    
    public static final DeferredHolder<WandAbility<?>, WandAbility<?>> DUMMY = WAND_ABILITIES.register("dummy",
            () -> new NoopAbility(0, 0, 0, false, null, 0xFF000000));

    public static final DeferredHolder<WandAbility<?>, ShootAbility> SHOOT_RAY = WAND_ABILITIES.register("shoot_ray",
            () -> new ShootAbility(0, 0, 0, true, ParticleTypes.SOUL_FIRE_FLAME, 0xFF54cbcf, 32, Vec3.ZERO));
    public static final DeferredHolder<WandAbility<?>, ShootAbility> SHOOT_HOLD_DOWN = WAND_ABILITIES.register("shoot_hold_down",
            () -> new ShootAbility(20, 20, 0, true, ParticleTypes.HAPPY_VILLAGER, 0xFF00ae2d, 16, Vec3.ZERO));
    public static final DeferredHolder<WandAbility<?>, ShootAbility> SHOOT_ANGRY_RAY = WAND_ABILITIES.register("shoot_angry_ray",
            () -> new ShootAbility(100, 200, 0, true, ExplodeAbility.PARTICLE, 0xFFAA0000, 8, ExplodeAbility.RANDOM_DEVIATION));
    public static final DeferredHolder<WandAbility<?>, ShootAbility> SHOOT_LIGHTNING = WAND_ABILITIES.register("shoot_lightning",
            () -> new ShootAbility(0, 25, 0, true, ParticleTypes.ELECTRIC_SPARK, 0xFFAAAAAA, 32, Vec3.ZERO));

    public static final DeferredHolder<WandAbility<?>, DamageAbility> DAMAGE = WAND_ABILITIES.register("damage", DamageAbility::new);
    public static final DeferredHolder<WandAbility<?>, ExplodeAbility> EXPLODE = WAND_ABILITIES.register("explode", ExplodeAbility::new);
    public static final DeferredHolder<WandAbility<?>, LightningAbility> LIGHTNING = WAND_ABILITIES.register("lightning", LightningAbility::new);
    public static final DeferredHolder<WandAbility<?>, HealAbility> HEAL = WAND_ABILITIES.register("heal", HealAbility::new);
    public static final DeferredHolder<WandAbility<?>, HealSelfAbility> HEAL_SELF = WAND_ABILITIES.register("heal_self", HealSelfAbility::new);

    public static final DeferredHolder<WandAbility<?>, SmallFireballAbility> SMALL_FIREBALL = WAND_ABILITIES.register("small_fireball", SmallFireballAbility::new);
    public static final DeferredHolder<WandAbility<?>, LargeFireballAbility> LARGE_FIREBALL = WAND_ABILITIES.register("large_fireball", LargeFireballAbility::new);

}
