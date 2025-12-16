package dev.screret.motm.core.mixin.vanilla;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructurePlaceSettings.class)
public interface StructurePlaceSettingsAccessor {

    @Accessor("random")
    RandomSource motm$getRandom();
}
