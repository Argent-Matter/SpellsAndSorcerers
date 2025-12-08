package dev.screret.motm.data;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class MOTMArmorMaterials {

    // spotless:off
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SOULSTEEL = register("soulsteel",
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 4);
                map.put(ArmorItem.Type.LEGGINGS, 7);
                map.put(ArmorItem.Type.CHESTPLATE, 9);
                map.put(ArmorItem.Type.HELMET, 4);
            }), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, () -> Ingredient.of(MOTMTags.Items.SOULSTEEL_INGOTS), 3.0F, 0.2F);

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense, int enchantmentValue,
                                                                         Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient,
                                                                         float toughness, float knockbackResistance) {
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(MOTMUtil.id(name)));
        return register(name, defense, enchantmentValue, equipSound, repairIngredient, toughness, knockbackResistance, layers);
    }

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense, int enchantmentValue,
                                                                         Holder<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient,
                                                                         float toughness, float knockbackResistance,
                                                                         List<ArmorMaterial.Layer> layers) {
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(defense, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance));
    }

    // spotless:on
}
