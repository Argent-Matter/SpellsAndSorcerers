package dev.screret.modularui;

import dev.screret.modularui.client.screen.ModularContainerMenu;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mariuszgromada.math.mxparser.License;

import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

@Mod(ModularUI.MOD_ID)
public class ModularUI {

    public static final String MOD_ID = "modularui";
    public static final String NAME = "Modular UI";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static final ResourceLocation TEMPLATE_LOCATION = ResourceLocation.fromNamespaceAndPath(MOD_ID, "");

    @Getter
    private static final DeltaTracker.Timer timer60Fps = new DeltaTracker.Timer(60f, 0, FloatUnaryOperator.identity());

    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, ModularUI.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ModularContainerMenu>> MODULAR_CONTAINER = MENU_TYPES.register(
            "modular",
            () -> IMenuTypeExtension.create(ModularContainerMenu::new));

    public ModularUI(IEventBus modEventBus, ModContainer modContainer) {
        MENU_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ModularUIConfig.CONFIG, MOD_ID + ".toml");
    }

    public static ResourceLocation id(String path) {
        return TEMPLATE_LOCATION.withPath(path);
    }

    /**
     * For async stuff use this, otherwise use {@link #isClientSide()}
     *
     * @return if the current thread is the client thread
     * @see #isClientSide()
     */
    @SuppressWarnings("ConstantValue")
    public static boolean isClientThread() {
        return isClientSide() && Minecraft.getInstance() != null && Minecraft.getInstance().isSameThread();
    }

    /**
     * @return if the game is the <strong>PHYSICAL</strong> client, e.g. not a dedicated server.
     * @apiNote Do not use this to check if you're currently on the server thread for side-specific actions!
     *          It does <strong>NOT</strong> work for that. Use {@link #isClientThread()} instead.
     * @see #isClientThread()
     */
    public static boolean isClientSide() {
        return FMLEnvironment.dist.isClient();
    }

    /**
     * @return whether we're running in a production environment
     */
    public static boolean isProd() {
        return FMLLoader.isProduction();
    }

    /**
     * @return whether we're not running in a production environment
     */
    public static boolean isDev() {
        return !isProd();
    }

    public enum Mods {

        CURIOS(ModIds.CURIOS),
        EMI(ModIds.EMI),
        JEI(ModIds.JEI),
        REI(ModIds.REI),
        SODIUM(ModIds.SODIUM),
        MOD_NAME_TOOLTIP(ModIds.MOD_NAME_TOOLTIP);

        public final String id;
        private boolean loaded = false;
        private boolean initialized = false;
        private final Predicate<ModContainer> extraLoadedCheck;

        Mods(String id) {
            this(id, null);
        }

        Mods(String id, @Nullable Predicate<ModContainer> extraLoadedCheck) {
            this.id = id;
            this.extraLoadedCheck = extraLoadedCheck;
        }

        public boolean isLoaded() {
            if (!this.initialized) {
                var modContainer = ModList.get().getModContainerById(this.id);
                this.loaded = modContainer.isPresent();
                if (this.loaded && this.extraLoadedCheck != null) {
                    this.loaded = this.extraLoadedCheck.test(modContainer.get());
                }
                this.initialized = true;
            }
            return this.loaded;
        }
    }

    public static class ModIds {

        public static final String EMI = "emi";
        public static final String JEI = "jei";
        public static final String REI = "roughlyenoughitems";
        public static final String CURIOS = "curios";
        public static final String SODIUM = "sodium";
        public static final String MOD_NAME_TOOLTIP = "modnametooltip";
    }

    static {
        License.iConfirmNonCommercialUse("ModularUI");
    }
}
