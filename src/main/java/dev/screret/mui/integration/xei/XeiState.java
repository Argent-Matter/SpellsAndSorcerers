package dev.screret.mui.integration.xei;

import dev.screret.mui.client.screen.ModularScreen;

import java.util.function.Predicate;

public enum XeiState implements Predicate<ModularScreen> {

    ENABLED {

        @Override
        public boolean test(ModularScreen screen) {
            return true;
        }
    },
    DISABLED {

        @Override
        public boolean test(ModularScreen screen) {
            return false;
        }
    },
    DEFAULT {

        @Override
        public boolean test(ModularScreen screen) {
            return !screen.isClientOnly();
        }
    }
}
