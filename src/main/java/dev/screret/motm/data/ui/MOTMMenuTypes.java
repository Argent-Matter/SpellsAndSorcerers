package dev.screret.motm.data.ui;

import dev.screret.motm.MagicOfTheMind;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MOTMMenuTypes {

    // spotless:off
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MagicOfTheMind.MOD_ID);


    // spotless:on

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> createSimple(MenuType.MenuSupplier<T> supplier) {
        return () -> new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS);
    }

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> create(IContainerFactory<T> supplier) {
        return () -> new MenuType<>(supplier, FeatureFlags.DEFAULT_FLAGS);
    }
}
