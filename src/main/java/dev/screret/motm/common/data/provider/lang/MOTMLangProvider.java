package dev.screret.motm.common.data.provider.lang;

import dev.screret.motm.common.data.util.LangUtil;
import dev.screret.motm.data.block.MOTMBlocks;
import dev.screret.motm.data.item.MOTMCreativeTabs;
import dev.screret.motm.data.item.MOTMItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.data.LanguageProvider;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

public class MOTMLangProvider extends LanguageProvider {

    // replace the original `data` map so we can replace its contents freely
    protected final Map<String, String> data = new TreeMap<>();
    protected final PackOutput output;
    protected final String modid;
    protected final String locale;

    public MOTMLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
        this.output = output;
        this.modid = modid;
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        // region menus
        addCreativeTab(MOTMCreativeTabs.CREATIVE_TAB, "Magic of the Mind");

        // endregion

        // region blocks
        addBlock(MOTMBlocks.PALANTIR, "Palantír");
        addBlock(MOTMBlocks.PORT_STONE, "Port Stone");
        // TODO add discovery system; use this
        add("block.motm.port_stone.undiscovered", "Mysterious Obelisk");

        addBlock(MOTMBlocks.SOULSTEEL_BLOCK, "Block of Soulsteel");
        addBlock(MOTMBlocks.GLINT_ORE, "Glint Ore");

        addBlock(MOTMBlocks.UNAWAKENED_MEMORYSTONE, "Unawakened Memorystone");
        addBlock(MOTMBlocks.UNAWAKENED_MEMORYSTONE_STAIRS, "Unawakened Memorystone Stairs");
        addBlock(MOTMBlocks.UNAWAKENED_MEMORYSTONE_SLAB, "Unawakened Memorystone Slab");
        addBlock(MOTMBlocks.UNAWAKENED_MEMORYSTONE_WALL, "Unawakened Memorystone Wall");

        addBlock(MOTMBlocks.MEMORYSTONE, "Memorystone");
        addBlock(MOTMBlocks.MEMORYSTONE_STAIRS, "Memorystone Stairs");
        addBlock(MOTMBlocks.MEMORYSTONE_SLAB, "Memorystone Slab");
        addBlock(MOTMBlocks.MEMORYSTONE_WALL, "Memorystone Wall");

        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE, "Polished Memorystone");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_STAIRS, "Polished Memorystone Stairs");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_SLAB, "Polished Memorystone Slab");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_WALL, "Polished Memorystone Wall");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_PRESSURE_PLATE, "Polished Memorystone Pressure Plate");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_BUTTON, "Polished Memorystone Button");

        addBlock(MOTMBlocks.CHISELED_POLISHED_MEMORYSTONE, "Chiseled Polished Memorystone");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS, "Polished Memorystone Bricks");
        addBlock(MOTMBlocks.CRACKED_POLISHED_MEMORYSTONE_BRICKS, "Cracked Polished Memorystone Bricks");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_STAIRS, "Polished Memorystone Brick Stairs");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_SLAB, "Polished Memorystone Brick Slab");
        addBlock(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_WALL, "Polished Memorystone Brick Wall");

        // endregion

        // region items
        addItem(MOTMItems.HANDLE, "Handle");
        addItem(MOTMItems.SOUL_BOTTLE, "Bottle o' Souls");
        addItem(MOTMItems.CLOUD_BOTTLE, "Bottled Clouds");
        addItem(MOTMItems.GLINT, "Glint");

        addItem(MOTMItems.SOULSTEEL_INGOT, "Soulsteel Ingot");
        addItem(MOTMItems.SOULSTEEL_NUGGET, "Soulsteel Nugget");
        addItem(MOTMItems.SOULSTEEL_HELMET, "Soulsteel Helmet");
        addItem(MOTMItems.SOULSTEEL_CHESTPLATE, "Soulsteel Chestplate");
        addItem(MOTMItems.SOULSTEEL_LEGGINGS, "Soulsteel Leggings");
        addItem(MOTMItems.SOULSTEEL_BOOTS, "Soulsteel Boots");
        addItem(MOTMItems.SOULSTEEL_SWORD, "Soulsteel Sword");
        addItem(MOTMItems.SOULSTEEL_SHOVEL, "Soulsteel Shovel");
        addItem(MOTMItems.SOULSTEEL_PICKAXE, "Soulsteel Pickaxe");
        addItem(MOTMItems.SOULSTEEL_AXE, "Soulsteel Axe");
        addItem(MOTMItems.SOULSTEEL_HOE, "Soulsteel Hoe");

        addItem(MOTMItems.THE_ONE_RING, "The One Ring");
        addMultiline("item.motm.the_one_ring.tooltip", """
                One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.
                You might find it hard to take off.
                """);
        // endregion

        // region entities

        // endregion

        // region effects & attributes

        // endregion

        // region misc

        add("motm.message.structure_load_error", "Could not load structure %s from the server as it does not exist.");
        add("motm.message.free_from_memory", "You're free from the memories of times gone by...");

        // endregion
    }

    // override the normal add() method to add to our map instead so we can skip the replacement check
    @Override
    public void add(@NotNull String key, @NotNull String value) {
        if (data.put(key, value) != null) {
            throw new IllegalStateException("Duplicate translation key " + key);
        }
    }

    public void replace(@NotNull String key, @NotNull String value) {
        data.put(key, value);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        addTranslations();

        if (!this.data.isEmpty()) {
            JsonObject json = new JsonObject();
            this.data.forEach(json::addProperty);

            return DataProvider.saveStable(cache, json, this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                    .resolve(this.modid).resolve("lang").resolve(this.locale + ".json"));
        }
        return CompletableFuture.allOf();
    }

    public void addCreativeTab(Supplier<? extends CreativeModeTab> key, String name) {
        add(key.get(), name);
    }

    public void add(CreativeModeTab key, String name) {
        Component component = key.getDisplayName();
        if (!(component.getContents() instanceof TranslatableContents contents)) {
            throw new IllegalArgumentException("Creative mode tab '" + BuiltInRegistries.CREATIVE_MODE_TAB.getKey(key) +
                    "' doesn't have a translatable name!");
        }
        add(contents.getKey(), name);
    }

    public void addAttribute(Supplier<? extends Attribute> key, String name) {
        add(key.get(), name);
    }

    public void add(Attribute key, String name) {
        add(key.getDescriptionId(), name);
    }

    /**
     * Registers multiple values under the same key with a given provider.<br>
     * <br>
     * For example, a cumbersome way to add translations would be the following:<br>
     *
     * <pre>
     * <code>provider.add("terminal.fluid_prospector.tier.0", "radius size 1");
     * provider.add("terminal.fluid_prospector.tier.1", "radius size 2");
     * provider.add("terminal.fluid_prospector.tier.2", "radius size 3");</code>
     * </pre>
     *
     * Instead, <code>addMultiline</code> can be used for the same result:
     *
     * <pre>
     * <code>addMultiline(provider, "terminal.fluid_prospector.tier", "radius size 1", "radius size 2", "radius size 3");</code>
     * </pre>
     *
     * In situations requiring a large number of generated translations, the
     * following could be used instead, which
     * generates translations for 100 tiers:
     *
     * <pre>
     * <code>addMultiline(provider, "terminal.fluid_prospector.tier", IntStream.of(100)
     *                 .map(i -> i + 1)
     *                 .mapToObj(Integer::toString)
     *                 .map(i -> "radius size " + i)
     *                 .toArray(String[]::new));</code>
     * </pre>
     *
     * @param key    Base key of the key-value-pairs. The real key for each
     *               translation will be appended by ".0" for
     *               the first, ".1" for the second, etc. This ensures that the
     *               keys are unique.
     * @param values All translation values.
     */
    protected void addMultiline(String key, String... values) {
        for (var i = 0; i < values.length; i++) {
            add(LangUtil.subKey(key, i), values[i]);
        }
    }

    /**
     * Adds one key-value-pair to the given lang provider per line in the given
     * multiline (a multiline is a String
     * containing newline characters).<br>
     * Example:
     *
     * <pre>
     * <code>addMultiline(provider, "gtceu.gui.overclock.enabled", "Overclocking Enabled.\nClick to Disable");</code>
     * </pre>
     *
     * This results in the following translations:<br>
     *
     * <pre>
     * <code>"gtceu.gui.overclock.enabled.0": "Overclocking Enabled.",
     * "gtceu.gui.overclock.enabled.1": "Click to Disable",</code>
     * </pre>
     *
     * @param key       Base key of the key-value-pair. The real key for each line
     *                  will be appended by ".0" for the
     *                  first line, ".1" for the second, etc. This ensures that the
     *                  keys are unique.
     * @param multiline The multiline string. It is a multiline because it contains
     *                  at least one newline character '\n'.
     */
    protected void addMultiline(String key, String multiline) {
        this.addMultiline(key, multiline.split("\n"));
    }
}
