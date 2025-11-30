package dev.screret.mitm.common.data.provider.lang;

import dev.screret.mitm.api.ability.WandAbility;
import dev.screret.mitm.common.data.util.LangUtil;
import dev.screret.mitm.data.*;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.data.LanguageProvider;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

public class MITMLangProvider extends LanguageProvider {

    // replace the original `data` map so we can replace its contents freely
    protected final Map<String, String> data = new TreeMap<>();
    protected final PackOutput output;
    protected final String modid;
    protected final String locale;

    public MITMLangProvider(PackOutput output, String modid, String locale) {
        super(output, modid, locale);
        this.output = output;
        this.modid = modid;
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        // region menus
        addCreativeTab(MITMCreativeTabs.CREATIVE_TAB, "Magic of the Mind");
        add("group.mitm.wands", "Wands");
        add("group.mitm.wand_cores", "Wand Cores");

        add("container.mitm.wand_table", "Wand Table");
        // endregion

        // region blocks
        addBlock(MITMBlocks.WAND_TABLE, "Wand Table");
        addBlock(MITMBlocks.SUMMONING_CIRCLE, "Summoning Circle");
        addBlock(MITMBlocks.PALANTIR, "Palantír");
        addBlock(MITMBlocks.POTION_DISTILLERY, "Potion Distillery");

        addBlock(MITMBlocks.SOULSTEEL_BLOCK, "Block of Soulsteel");
        addBlock(MITMBlocks.GLINT_ORE, "Glint Ore");
        // endregion

        // region items
        addItem(MITMItems.HANDLE, "Handle");
        addItem(MITMItems.SOUL_BOTTLE, "Bottle o' Souls");
        addItem(MITMItems.CLOUD_BOTTLE, "Bottled Clouds");
        addItem(MITMItems.CTHULHU_EYE, "Eye of Cthulhu");
        addItem(MITMItems.GLINT, "Glint");

        addItem(MITMItems.SOULSTEEL_INGOT, "Soulsteel Ingot");
        addItem(MITMItems.SOULSTEEL_NUGGET, "Soulsteel Nugget");
        addItem(MITMItems.SOULSTEEL_HELMET, "Soulsteel Helmet");
        addItem(MITMItems.SOULSTEEL_CHESTPLATE, "Soulsteel Chestplate");
        addItem(MITMItems.SOULSTEEL_LEGGINGS, "Soulsteel Leggings");
        addItem(MITMItems.SOULSTEEL_BOOTS, "Soulsteel Boots");
        addItem(MITMItems.SOULSTEEL_SWORD, "Soulsteel Sword");
        addItem(MITMItems.SOULSTEEL_SHOVEL, "Soulsteel Shovel");
        addItem(MITMItems.SOULSTEEL_PICKAXE, "Soulsteel Pickaxe");
        addItem(MITMItems.SOULSTEEL_AXE, "Soulsteel Axe");
        addItem(MITMItems.SOULSTEEL_HOE, "Soulsteel Hoe");

        addItem(MITMItems.WAND, "Wand of %s");
        add("tooltip.mitm.joiner", ", ");
        add("tooltip.mitm.joiner.last", " and ");
        addItem(MITMItems.WAND_CORE, "%s Core");

        addItem(MITMItems.THE_ONE_RING, "The One Ring");
        addMultiline("item.mitm.the_one_ring.tooltip", """
                One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.
                You might find it hard to take off.
                """);

        addItem(MITMItems.WIZARD_SPAWN_EGG, "Wizard Spawn Egg");
        addItem(MITMItems.BOSS_WIZARD_SPAWN_EGG, "Necromantic Sorcerer Spawn Egg");
        // endregion

        // region wand abilities
        addAbility(MITMWandAbilities.DUMMY, "Absolutely No");
        addAbility(MITMWandAbilities.SHOOT_HOLD_DOWN, "Continuous");
        addAbility(MITMWandAbilities.SHOOT_LIGHTNING, "Sparky");
        addAbility(MITMWandAbilities.DAMAGE, "Hurting");
        addAbility(MITMWandAbilities.EXPLODE, "Explosion");
        addAbility(MITMWandAbilities.LIGHTNING, "Lightning");
        addAbility(MITMWandAbilities.HEAL, "Healing");
        addAbility(MITMWandAbilities.HEAL_SELF, "Self-healing");
        add("ability.mitm.sight", "Sight");
        add("ability.mitm.transmutation", "Transmutation");
        addAbility(MITMWandAbilities.SMALL_FIREBALL, "Small Fireballs");
        addAbility(MITMWandAbilities.LARGE_FIREBALL, "Fireballs");
        // endregion

        // region entities
        addEntityType(MITMEntityTypes.WIZARD, "Wizard");
        addEntityType(MITMEntityTypes.BOSS_WIZARD, "Necromantic Sorcerer");
        // endregion

        // effects
        addEffect(MITMMobEffects.MANA, "Mana Boost");
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

    public void addAbility(Supplier<? extends WandAbility<?>> key, String name) {
        add(key.get(), name);
    }

    public void add(WandAbility<?> key, String name) {
        add(key.getKey().toLanguageKey("ability"), name);
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
