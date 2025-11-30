package dev.screret.mitm.common.data.provider.lang;

import dev.screret.mitm.common.data.util.LangUtil;
import dev.screret.mitm.data.MITMItems;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import com.google.gson.JsonObject;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

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
        addItem(MITMItems.WAND, "Wand of ");
        add("tooltip.mitm.joiner", ", ");

        addMultiline("item.mitm.the_one_ring.tooltip", """
                One Ring to rule them all, One Ring to find them, One Ring to bring them all and in the darkness bind them.
                You might find it hard to take off.
                """);
    }

    // override the normal add() method to add to our map instead and skip the replacement check
    @Override
    public void add(@NotNull String key, @NotNull String value) {
        this.data.put(key, value);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        addTranslations();

        if (!this.data.isEmpty()) {
            JsonObject json = new JsonObject();
            this.data.forEach(json::addProperty);

            return DataProvider.saveStable(cache, json,
                    this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                            .resolve(this.modid).resolve("lang").resolve(this.locale + ".json"));
        }
        return CompletableFuture.allOf();
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
