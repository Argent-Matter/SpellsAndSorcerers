package dev.screret.mitm.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.MITMUtil;

import java.util.function.Supplier;

public class MITMCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MagicOfTheMind.MODID);

    public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("spellsandsorcerers", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MagicOfTheMind.MODID))
            .icon(() -> MITMItems.WAND.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.acceptAll(MITMUtil.CUSTOM_WANDS.values());
                output.acceptAll(MITMUtil.CUSTOM_WAND_CORES.values());
                for (DeferredHolder<Item, ? extends Item> item : MITMItems.ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            })
            .build());

}
