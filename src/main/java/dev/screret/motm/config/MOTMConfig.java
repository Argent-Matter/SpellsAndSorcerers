package dev.screret.motm.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MOTMConfig {

    public static class Client {

        public static final ModConfigSpec clientSpec;

        private static void setupConfig(ModConfigSpec.Builder builder) {
            builder.comment("Spells & Sorcerers Client Configuration");
        }

        static {
            ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
            setupConfig(configBuilder);
            clientSpec = configBuilder.build();
        }
    }

    public static class Server {

        public static final ModConfigSpec serverSpec;
        public static ModConfigSpec.BooleanValue enableQthulhuEyeConversion;

        private static void setupConfig(ModConfigSpec.Builder builder) {
            builder.comment("Spells & Sorcerers Server Configuration")
                    .push("server");

            enableQthulhuEyeConversion = builder
                    .comment("Does the Eye of Qthulhu convert blocks?")
                    .translation("motm.configgui.enableQthulhuEyeConversion")
                    .define("enableQthulhuEyeConversion", true);
            builder.pop();
        }

        static {
            ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
            setupConfig(configBuilder);
            serverSpec = configBuilder.build();
        }
    }
}
