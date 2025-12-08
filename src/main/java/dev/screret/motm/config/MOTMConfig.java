package dev.screret.motm.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MOTMConfig {

    public static class Client {

        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        public static final ModConfigSpec CLIENT_CONFIG;

        // add config fields here

        static {
            CLIENT_CONFIG = BUILDER.build();
        }
    }

    public static class Server {

        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        public static final ModConfigSpec SERVER_CONFIG;

        // add config fields here

        static {
            SERVER_CONFIG = BUILDER.build();
        }
    }
}
