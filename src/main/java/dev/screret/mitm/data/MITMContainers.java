package dev.screret.mitm.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.common.menu.container.PotionDistilleryMenu;
import dev.screret.mitm.common.menu.container.WandTableMenu;

import java.util.function.Supplier;

public class MITMContainers {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MagicOfTheMind.MODID);

    public static final Supplier<MenuType<WandTableMenu>> WAND_TABLE = MENU_TYPES.register("wand_table", () -> IMenuTypeExtension.create((id, inv, extraData) -> new WandTableMenu(id, inv)));
    public static final Supplier<MenuType<PotionDistilleryMenu>> POTION_DISTILLERY = MENU_TYPES.register("potion_distillery", () -> IMenuTypeExtension.create((id, inv, extraData) -> new PotionDistilleryMenu(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos(), MITMBlockEntities.POTION_DISTILLERY.get()).get())));
}
