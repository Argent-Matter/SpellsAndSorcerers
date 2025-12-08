package dev.screret.motm.data;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MOTMCreativeTabs {

    // spotless:off
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MagicOfTheMind.MOD_ID);

    public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("motm", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MagicOfTheMind.MOD_ID))
            .icon(() -> MOTMItems.WAND.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.acceptAll(MOTMUtil.CUSTOM_WANDS.values());
                output.acceptAll(MOTMUtil.CUSTOM_WAND_CORES.values());
                for (DeferredHolder<Item, ? extends Item> item : MOTMItems.ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            })
            .build());
    // spotless:on
}
