package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.entity.Elderling;
import dev.screret.motm.common.item.OneRingItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMEntityTypes {

    // spotless:off
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<OneRingItem.RingItemEntity>> RING_ITEM = ENTITY_TYPES.register("the_one_ring",
            () -> EntityType.Builder.of(OneRingItem.RingItemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20).noSummon()
                    .build("motm_the_one_ring"));

    // TODO should this be smaller?
    public static final DeferredHolder<EntityType<?>, EntityType<Elderling>> ELDERLING = ENTITY_TYPES.register("elderling",
            () -> EntityType.Builder.of(Elderling::new, MobCategory.MISC)
                    .sized(1.0f, 3.0f).eyeHeight(3.8125f)
                    .clientTrackingRange(10)
                    .build("motm_elderling"));

    // spotless:on
}
