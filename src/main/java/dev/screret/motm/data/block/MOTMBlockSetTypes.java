package dev.screret.motm.data.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class MOTMBlockSetTypes {

    // spotless:off

    public static final BlockSetType POLISHED_MEMORYSTONE = BlockSetType.register(new BlockSetType("polished_memorystone",
            true, true, false,
            BlockSetType.PressurePlateSensitivity.MOBS,
            SoundType.GILDED_BLACKSTONE,
            SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON)
    );

    // spotless:on
}
