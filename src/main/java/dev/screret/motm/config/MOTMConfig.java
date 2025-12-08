package dev.screret.motm.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MOTMConfig {

    public static class Client {

        public static final ModConfigSpec clientSpec;

        private static void setupConfig(ModConfigSpec.Builder builder) {
            builder.comment("Magic of the Mind Client Configuration");
        }

        static {
            ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
            setupConfig(configBuilder);
            clientSpec = configBuilder.build();
        }
    }

    public static class Server {

        public static final ModConfigSpec serverSpec;

        private static void setupConfig(ModConfigSpec.Builder builder) {
            builder.comment("Magic of the Mind Server Configuration");
        }

        static {
            ModConfigSpec.Builder configBuilder = new ModConfigSpec.Builder();
            setupConfig(configBuilder);
            serverSpec = configBuilder.build();
        }
    }
}
