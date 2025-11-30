package dev.screret.motm.data;

import dev.screret.motm.MOTMUtil;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class MOTMTags {

    public static final class Items {

        public static final TagKey<Item> GLASS_BOTTLES = forgeTag("glass_bottles");
        public static final TagKey<Item> GLINT_ORES = forgeTag("ores/glint");
        public static final TagKey<Item> GLINT_GEMS = forgeTag("gems/glint");
        public static final TagKey<Item> SOULSTEEL_INGOTS = forgeTag("ingots/soulsteel");
        public static final TagKey<Item> SOULSTEEL_NUGGETS = forgeTag("nuggets/soulsteel");
        public static final TagKey<Item> SOULSTEEL_BLOCKS = forgeTag("storage_blocks/soulsteel");

        public static final TagKey<Item> BOSS_SUMMON_ITEMS = tag("boss_summon_items");

        public static final TagKey<Item> ENCHANTABLE_WITH_QUICK_CHARGE = tag("enchantable_with/quick_charge");
        public static final TagKey<Item> ENCHANTABLE_WITH_POWER = tag("enchantable_with/power");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(MOTMUtil.id(name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
        }
    }

    public static final class Blocks {

        public static final TagKey<Block> GLINT_ORES = forgeTag("ores/glint");
        public static final TagKey<Block> SOULSTEEL_BLOCKS = forgeTag("storage_blocks/soulsteel");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(MOTMUtil.id(name));
        }

        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("forge", name));
        }
    }

    public static final class Biomes {

        public static final TagKey<Biome> HAS_RITUAL_SPOT = tag("has_structure/ritual_spot");
        public static final TagKey<Biome> HAS_WIZARD_TOWER = tag("has_structure/wizard_tower");

        private static TagKey<Biome> tag(String name) {
            return create(MOTMUtil.id(name));
        }

        private static TagKey<Biome> create(ResourceLocation name) {
            return TagKey.create(Registries.BIOME, name);
        }
    }
}
