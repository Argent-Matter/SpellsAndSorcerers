package screret.sas.creativetab;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.item.ModItems;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, SpellsAndSorcerers.MODID);

    public static final Supplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("spellsandsorcerers", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + SpellsAndSorcerers.MODID))
            .icon(() -> ModItems.WAND.get().getDefaultInstance())
            .displayItems((pParameters, output) -> {
                output.acceptAll(Util.customWands.values());
                output.acceptAll(Util.customWandCores.values());
                for (DeferredHolder<Item, ? extends Item> item : ModItems.ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            })
            .build());

}
