package dev.screret.mitm.common.data.provider.model;

import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.data.MITMItems;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MITMItemModelProvider extends ItemModelProvider {

    public MITMItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MagicOfTheMind.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(MITMItems.HANDLE.get());
        basicItem(MITMItems.SOUL_BOTTLE.get());
        // basicItem(MITMItems.CLOUD_BOTTLE.get());

        basicItem(MITMItems.SOULSTEEL_INGOT.get());
        basicItem(MITMItems.SOULSTEEL_NUGGET.get());
        basicItem(MITMItems.GLINT.get());

        basicItem(MITMItems.THE_ONE_RING.get());

        basicItem(MITMItems.SOULSTEEL_HELMET.get());
        basicItem(MITMItems.SOULSTEEL_CHESTPLATE.get());
        basicItem(MITMItems.SOULSTEEL_LEGGINGS.get());
        basicItem(MITMItems.SOULSTEEL_BOOTS.get());

        handheldItem(MITMItems.SOULSTEEL_SWORD.get());
        handheldItem(MITMItems.SOULSTEEL_SHOVEL.get());
        handheldItem(MITMItems.SOULSTEEL_PICKAXE.get());
        handheldItem(MITMItems.SOULSTEEL_AXE.get());
        handheldItem(MITMItems.SOULSTEEL_HOE.get());

        spawnEggItem(MITMItems.WIZARD_SPAWN_EGG.get());
        spawnEggItem(MITMItems.BOSS_WIZARD_SPAWN_EGG.get());
    }
}
