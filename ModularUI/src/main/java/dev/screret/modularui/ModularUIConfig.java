package dev.screret.modularui;

import dev.screret.modularui.client.screen.RichTooltip;

import net.minecraft.ChatFormatting;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;

import java.util.Objects;

public class ModularUIConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec CONFIG;

    public static final IntValue DEFAULT_SCROLL_SPEED = BUILDER
            .comment("Amount of pixels scrolled")
            .translation("config.modularui.defaultScrollSpeed")
            .defineInRange("defaultScrollSpeed", 30, 1, 100);
    public static final BooleanValue SMOOTH_PROGRESS_BARS = BUILDER
            .comment(
                    "If progress bars should step in texture pixels or screen pixels. (Screen pixels are way smaller and therefore smoother)")
            .translation("config.modularui.smoothProgressBars")
            .define("smoothProgressBars", false);
    // Default direction
    public static final EnumValue<RichTooltip.Pos> TOOLTIP_POS = BUILDER
            .comment("Default tooltip position around the widget or its panel.")
            .translation("config.modularui.tooltipPos")
            .defineEnum("tooltipPos", RichTooltip.Pos.NEXT_TO_MOUSE);
    public static final BooleanValue ESC_RESTORES_LAST_TEXT = BUILDER
            .comment("If true, pressing ESC key in the text field will restore the last text instead of confirming current one.")
            .translation("config.modularui.escRestoresLastText")
            .define("escRestoresLastText", false);
    public static final BooleanValue GUI_DEBUG_MODE = BUILDER
            .comment("If true, widget outlines and widget information will be drawn.")
            .translation("config.modularui.guiDebugMode")
            .define("guiDebugMode", !FMLLoader.isProduction());
    public static final BooleanValue USE_DARK_THEME_BY_DEFAULT = BUILDER
            .comment("If true and not specified otherwise, screens will try to use the 'vanilla_dark' theme.")
            .translation("config.modularui.useDarkThemeByDefault")
            .define("useDarkThemeByDefault", false);
    public static final BooleanValue ENABLE_TEST_GUIS = BUILDER
            .comment("Enables a test block, test item with a test gui and opening a gui by right clicking a diamond.")
            .translation("config.modularui.enableTestGuis")
            .gameRestart()
            .define("enableTestGuis", !FMLLoader.isProduction());
    public static final BooleanValue ENABLE_TEST_OVERLAYS = BUILDER
            .comment("Enables a test overlay shown on title screen and watermark shown on every GuiContainer.")
            .translation("config.modularui.enableTestOverlays")
            .gameRestart()
            .define("enableTestOverlays", false);
    public static final BooleanValue REPLACE_VANILLA_TOOLTIPS = BUILDER
            .comment("If true, vanilla tooltip will be replaced with MUI's RichTooltip")
            .translation("config.modularui.replaceVanillaTooltips")
            .define("replaceVanillaTooltips", false);
    public static final ConfigValue<String> MOD_NAME_FORMAT = BUILDER
            .comment("The format prefix of the mod name tooltip line.", "Default: 'blue italic' (converted to §9§o)")
            .translation("config.modularui.modNameFormat")
            .define("modNameFormat", ChatFormatting.BLUE.getName() + " " + ChatFormatting.ITALIC.getName());

    static {
        CONFIG = BUILDER.build();
    }

    public static int getDefaultScrollSpeed() {
        return DEFAULT_SCROLL_SPEED.getAsInt();
    }

    public static boolean isSmoothProgressBars() {
        return SMOOTH_PROGRESS_BARS.getAsBoolean();
    }

    public static RichTooltip.Pos getTooltipPos() {
        return TOOLTIP_POS.get();
    }

    public static boolean escRestoresLastText() {
        return ESC_RESTORES_LAST_TEXT.getAsBoolean();
    }

    public static boolean isGuiDebugMode() {
        return GUI_DEBUG_MODE.getAsBoolean();
    }

    public static boolean useDarkThemeByDefault() {
        return USE_DARK_THEME_BY_DEFAULT.getAsBoolean();
    }

    public static boolean enableTestGuis() {
        return ENABLE_TEST_GUIS.getAsBoolean();
    }

    public static boolean enableTestOverlays() {
        return ENABLE_TEST_OVERLAYS.getAsBoolean();
    }

    public static boolean replaceVanillaTooltips() {
        return REPLACE_VANILLA_TOOLTIPS.getAsBoolean();
    }

    private static String lastValue = null;
    private static ChatFormatting[] lastParsed = null;

    public static ChatFormatting[] getModNameFormat() {
        String unparsed = MOD_NAME_FORMAT.get();
        if (!Objects.equals(unparsed, lastValue)) {
            lastValue = unparsed;
            String[] split = lastValue.split("\\s");
            lastParsed = new ChatFormatting[split.length];
            for (int i = 0; i < split.length; i++) {
                String name = split[i];
                lastParsed[i] = ChatFormatting.getByName(name);
            }
        }
        return lastParsed;
    }
}
