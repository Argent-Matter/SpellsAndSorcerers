package dev.screret.motm.core.mixin.accessor;

import dev.screret.motm.core.ext.IJigsawStructureExtension;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public class JigsawStructureMixin implements IJigsawStructureExtension {

    @Shadow
    @Final
    private Holder<StructureTemplatePool> startPool;
    @Shadow
    @Final
    private Optional<ResourceLocation> startJigsawName;
    @Shadow
    @Final
    private int maxDepth;

    public Holder<StructureTemplatePool> motm$getStartPool() {
        return this.startPool;
    }

    public Optional<ResourceLocation> motm$getStartJigsawName() {
        return this.startJigsawName;
    }

    public int motm$getMaxDepth() {
        return this.maxDepth;
    }
}
