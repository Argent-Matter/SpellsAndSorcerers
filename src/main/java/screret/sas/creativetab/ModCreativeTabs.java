package screret.sas.creativetab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.item.ModItems;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpellsAndSorcerers.MODID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("spellsandsorcerers", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + SpellsAndSorcerers.MODID))
            .icon(() -> ModItems.WAND.get().getDefaultInstance())
            .displayItems((pParameters, output) -> {
                output.acceptAll(Util.CUSTOM_WANDS.values());
                output.acceptAll(Util.CUSTOM_WAND_CORES.values());
                for (RegistryObject<Item> item : ModItems.ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            })
            .build());

}
