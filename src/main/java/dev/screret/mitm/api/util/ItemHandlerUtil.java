package dev.screret.mitm.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemHandlerUtil {

    public static void dropContents(Level level, BlockPos pos, IItemHandler inventory) {
        dropContents(level, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    public static void dropContents(Level level, Entity entityAt, IItemHandler inventory) {
        dropContents(level, entityAt.getX(), entityAt.getY(), entityAt.getZ(), inventory);
    }

    private static void dropContents(Level level, double x, double y, double z, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            Containers.dropItemStack(level, x, y, z, inventory.getStackInSlot(i));
        }
    }
}
