package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.menu.container.PotionDistilleryMenu;
import dev.screret.motm.common.menu.container.WandTableMenu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MOTMContainers {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU,
            MagicOfTheMind.MODID);

    public static final Supplier<MenuType<WandTableMenu>> WAND_TABLE = MENU_TYPES.register("wand_table",
            () -> IMenuTypeExtension.create((id, inv, extraData) -> new WandTableMenu(id, inv)));
    public static final Supplier<MenuType<PotionDistilleryMenu>> POTION_DISTILLERY = MENU_TYPES.register("potion_distillery",
            () -> IMenuTypeExtension.create((id, inv, extraData) -> new PotionDistilleryMenu(id, inv, inv.player.level()
                    .getBlockEntity(extraData.readBlockPos(), MOTMBlockEntities.POTION_DISTILLERY.get()).get())));
}
