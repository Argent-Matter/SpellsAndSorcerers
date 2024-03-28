package screret.sas.ability;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import screret.sas.ability.ability.*;
import screret.sas.api.wand.ability.WandAbility;

import java.util.function.Supplier;

import static screret.sas.api.wand.ability.WandAbilityRegistry.WAND_ABILITIES;


public class ModWandAbilities {
    public static final Supplier<WandAbility> DUMMY = WAND_ABILITIES.register("dummy", () -> new WandAbility(0, 0, 0, false, null, 0xFF000000));

    public static final Supplier<WandAbility> SHOOT_RAY = WAND_ABILITIES.register("shoot_ray", () -> new ShootAbility(0, 0, 0, true, ParticleTypes.SOUL_FIRE_FLAME, 0xFF54cbcf, 32, Vec3.ZERO));
    public static final Supplier<WandAbility> SHOOT_HOLD_DOWN = WAND_ABILITIES.register("shoot_hold_down", () -> new ShootAbility(20, 20, 0, true, ParticleTypes.HAPPY_VILLAGER, 0xFF00ae2d, 16, Vec3.ZERO));
    public static final Supplier<WandAbility> SHOOT_ANGRY_RAY = WAND_ABILITIES.register("shoot_angry_ray", () -> new ShootAbility(100, 200, 0, true, ExplodeAbility.PARTICLE, 0xFFAA0000, 8, ExplodeAbility.RANDOM_DEVIATION));
    public static final Supplier<WandAbility> SHOOT_LIGHTNING = WAND_ABILITIES.register("shoot_lightning", () -> new ShootAbility(0, 25, 0, true, ParticleTypes.ELECTRIC_SPARK, 0xFFAAAAAA, 32, Vec3.ZERO));

    public static final Supplier<WandAbility> DAMAGE = WAND_ABILITIES.register("damage", DamageAbility::new);
    public static final Supplier<WandAbility> EXPLODE = WAND_ABILITIES.register("explode", ExplodeAbility::new);
    public static final Supplier<WandAbility> LIGHTNING = WAND_ABILITIES.register("lightning", LightningAbility::new);
    public static final Supplier<WandAbility> HEAL = WAND_ABILITIES.register("heal", HealAbility::new);
    public static final Supplier<WandAbility> HEAL_SELF = WAND_ABILITIES.register("heal_self", HealSelfAbility::new);

    public static final Supplier<WandAbility> SMALL_FIREBALL = WAND_ABILITIES.register("small_fireball", SmallFireballAbility::new);
    public static final Supplier<WandAbility> LARGE_FIREBALL = WAND_ABILITIES.register("large_fireball", LargeFireballAbility::new);

    public static void init() {
    }

}
