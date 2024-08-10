package screret.sas.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SASConfig {

    public static class Client {
        public static final ForgeConfigSpec clientSpec;

        public static ForgeConfigSpec.IntValue manaBarX;
        public static ForgeConfigSpec.IntValue manaBarY;

        private static void setupConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Spells & Sorcerers Client Configuration")
                    .push("client");

            manaBarX = builder
                    .comment("Mana bar's X position from center of screen.")
                    .translation("sas.configgui.manaBarX")
                    .defineInRange("manaBarX", -91, Integer.MIN_VALUE, Integer.MAX_VALUE);
            manaBarY = builder
                    .comment("Mana bar's Y position from bottom of screen.")
                    .translation("sas.configgui.manaBarY")
                    .defineInRange("manaBarY", 57, Integer.MIN_VALUE, Integer.MAX_VALUE);

            builder.pop();
        }

        static {
            ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
            setupConfig(configBuilder);
            clientSpec = configBuilder.build();
        }
    }

    public static class Server {
        public static final ForgeConfigSpec serverSpec;
        public static ForgeConfigSpec.BooleanValue useMana;
        public static ForgeConfigSpec.BooleanValue armorGiveEffects;
        public static ForgeConfigSpec.BooleanValue enableQthulhuEyeConversion;

        public static ForgeConfigSpec.IntValue maxDefaultMana;

        public static ForgeConfigSpec.BooleanValue dropWandCores;

        private static void setupConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Spells & Sorcerers Server Configuration")
                    .push("server");

            useMana = builder
                    .comment("Is Mana used?")
                    .translation("sas.configgui.useMana")
                    .define("useMana", true);
            armorGiveEffects = builder
                    .comment("Does Soulsteel armor give potion effects?")
                    .translation("sas.configgui.armorGiveEffects")
                    .define("armorGiveEffects", true);
            maxDefaultMana = builder
                    .comment("Maximum default mana (no potion effects)")
                    .translation("sas.configgui.maxDefaultMana")
                    .defineInRange("maxDefaultMana", 100, 0, Integer.MAX_VALUE);
            dropWandCores = builder
                    .comment("Do wizards drop wand cores?")
                    .translation("sas.configgui.dropWandCores")
                    .define("dropWandCores", true);
            enableQthulhuEyeConversion = builder
                    .comment("Does the Eye of Qthulhu convert blocks?")
                    .translation("sas.configgui.enableQthulhuEyeConversion")
                    .define("enableQthulhuEyeConversion", true);
            builder.pop();
        }

        static {
            ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
            setupConfig(configBuilder);
            serverSpec = configBuilder.build();
        }
    }
}
