package dev.screret.motm.common.data.provider.model;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.data.MOTMItems;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MOTMItemModelProvider extends ItemModelProvider {

    public MOTMItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MagicOfTheMind.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(MOTMItems.HANDLE.get());
        basicItem(MOTMItems.SOUL_BOTTLE.get());
        // basicItem(MOTMItems.CLOUD_BOTTLE.get());

        basicItem(MOTMItems.SOULSTEEL_INGOT.get());
        basicItem(MOTMItems.SOULSTEEL_NUGGET.get());
        basicItem(MOTMItems.GLINT.get());

        basicItem(MOTMItems.THE_ONE_RING.get());

        basicItem(MOTMItems.SOULSTEEL_HELMET.get());
        basicItem(MOTMItems.SOULSTEEL_CHESTPLATE.get());
        basicItem(MOTMItems.SOULSTEEL_LEGGINGS.get());
        basicItem(MOTMItems.SOULSTEEL_BOOTS.get());

        handheldItem(MOTMItems.SOULSTEEL_SWORD.get());
        handheldItem(MOTMItems.SOULSTEEL_SHOVEL.get());
        handheldItem(MOTMItems.SOULSTEEL_PICKAXE.get());
        handheldItem(MOTMItems.SOULSTEEL_AXE.get());
        handheldItem(MOTMItems.SOULSTEEL_HOE.get());
    }
}
