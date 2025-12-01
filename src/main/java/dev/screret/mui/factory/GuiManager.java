package dev.screret.mui.factory;

import dev.screret.mui.ModularUI;
import dev.screret.mui.api.IMuiScreen;
import dev.screret.mui.api.MCHelper;
import dev.screret.mui.api.UIFactory;
import dev.screret.mui.api.XeiSettings;
import dev.screret.mui.core.mixins.ServerPlayerAccessor;
import dev.screret.mui.network.packets.OpenGuiPacket;
import dev.screret.mui.value.sync.ModularSyncManager;
import dev.screret.mui.value.sync.PanelSyncManager;
import dev.screret.mui.widget.WidgetTree;
import dev.screret.mui.client.screen.*;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@EventBusSubscriber(modid = ModularUI.MOD_ID)
public class GuiManager {

    private static final Object2ObjectMap<ResourceLocation, UIFactory<?>> FACTORIES = new Object2ObjectOpenHashMap<>(
            16);

    private static final List<Player> openedContainers = new ArrayList<>(4);

    public static void registerFactory(UIFactory<?> factory) {
        Objects.requireNonNull(factory);
        ResourceLocation name = Objects.requireNonNull(factory.getFactoryName());
        if (FACTORIES.containsKey(name)) {
            throw new IllegalArgumentException("Factory with name '" + name + "' is already registered!");
        }
        FACTORIES.put(name, factory);
    }

    public static @NotNull UIFactory<?> getFactory(ResourceLocation name) {
        UIFactory<?> factory = FACTORIES.get(name);
        if (factory == null) throw new NoSuchElementException("No UI factory for name '" + name + "' found!");
        return factory;
    }

    public static boolean hasFactory(ResourceLocation name) {
        return FACTORIES.containsKey(name);
    }

    public static <T extends GuiData> void open(@NotNull UIFactory<T> factory, @NotNull T guiData,
                                                ServerPlayer player) {
        if (player instanceof FakePlayer || openedContainers.contains(player)) return;
        openedContainers.add(player);
        // create panel, collect sync handlers and create menu
        UISettings settings = new UISettings(XeiSettings.DUMMY);
        settings.defaultCanInteractWith(factory, guiData);
        PanelSyncManager syncManager = new PanelSyncManager(false);
        ModularPanel panel = factory.createPanel(guiData, syncManager, settings);
        WidgetTree.collectSyncValues(syncManager, panel);

        // create the menu
        ((ServerPlayerAccessor) player).invokeNextContainerCounter();
        if (player.containerMenu != player.inventoryMenu) {
            player.closeContainer();
        }
        int windowId = ((ServerPlayerAccessor) player).getContainerCounter();
        ModularContainerMenu menu = settings.hasContainer() ? settings.createContainer(windowId) :
                factory.createContainer(windowId);
        menu.construct(player, syncManager, settings, panel.getName(), guiData);

        // sync to client
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        factory.writeGuiData(guiData, buffer);
        PacketDistributor.sendToPlayer(player, new OpenGuiPacket<>(windowId, factory, buffer));
        // open the menu // this mimics forge behaviour
        ((ServerPlayerAccessor) player).invokeInitMenu(menu);
        player.containerMenu = menu;
        // finally invoke event
        NeoForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, menu));
    }

    @ApiStatus.Internal
    @OnlyIn(Dist.CLIENT)
    public static <T extends GuiData> void openFromClient(int windowId, @NotNull UIFactory<T> factory,
                                                          @NotNull FriendlyByteBuf data, @NotNull Player player) {
        T guiData = factory.readGuiData(player, data);
        UISettings settings = new UISettings();
        settings.defaultCanInteractWith(factory, guiData);
        PanelSyncManager syncManager = new PanelSyncManager(true);
        ModularPanel panel = factory.createPanel(guiData, syncManager, settings);
        WidgetTree.collectSyncValues(syncManager, panel);
        ModularScreen screen = factory.createScreen(guiData, panel);
        screen.getContext().setSettings(settings);
        ModularContainerMenu container = settings.hasContainer() ? settings.createContainer(windowId) :
                factory.createContainer(windowId);
        container.construct(player, syncManager, settings, panel.getName(), guiData);
        IMuiScreen wrapper = factory.createScreenWrapper(container, screen);
        if (!(wrapper.getWrappedScreen() instanceof AbstractContainerScreen<?> guiContainer)) {
            throw new IllegalStateException("The wrapping screen must be a GuiContainer for synced GUIs!");
        }
        if (guiContainer.getMenu() != container)
            throw new IllegalStateException("Custom Containers are not yet allowed!");
        MCHelper.setScreen(wrapper.getWrappedScreen());
        player.containerMenu = guiContainer.getMenu();
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends GuiData> void openFromClient(@NotNull UIFactory<T> factory, @NotNull T guiData) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        factory.writeGuiData(guiData, buffer);
        PacketDistributor.sendToServer(new OpenGuiPacket<>(0, factory, buffer));
    }

    @OnlyIn(Dist.CLIENT)
    static void openScreen(ModularScreen screen, UISettings settings) {
        if (screen.getScreenWrapper() != null &&
                MCHelper.getCurrentScreen() == screen.getScreenWrapper().getWrappedScreen()) {
            // already open
            return;
        }
        screen.getContext().setSettings(settings);
        Screen guiScreen;
        if (settings.hasContainer()) {
            ModularContainerMenu container = settings.createContainer(0);
            container.constructClientOnly();
            guiScreen = new ContainerScreenWrapper(container, screen);
        } else {
            guiScreen = new ScreenWrapper(screen);
        }
        MCHelper.setScreen(guiScreen);
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        openedContainers.clear();
    }

    @SubscribeEvent
    public static void onOpenContainer(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof ModularContainerMenu modular) {
            ModularSyncManager syncManager = modular.getSyncManager();
            if (syncManager != null) {
                syncManager.onOpen();
            }
        }
    }
}
