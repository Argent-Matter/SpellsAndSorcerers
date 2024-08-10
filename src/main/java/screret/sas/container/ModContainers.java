package screret.sas.container;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.blockentity.blockentity.PotionDistilleryBE;
import screret.sas.container.container.PotionDistilleryMenu;
import screret.sas.container.container.WandTableMenu;

public class ModContainers {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SpellsAndSorcerers.MODID);

    public static final RegistryObject<MenuType<WandTableMenu>> WAND_TABLE = MENU_TYPES.register("wand_table", () -> IForgeMenuType.create((id, inv, extraData) -> new WandTableMenu(id, inv)));
    public static final RegistryObject<MenuType<PotionDistilleryMenu>> POTION_DISTILLERY = MENU_TYPES.register("potion_distillery", () -> IForgeMenuType.create((id, inv, extraData) -> new PotionDistilleryMenu(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos(), ModBlockEntities.POTION_DISTILLERY_BE.get()).get())));
}
