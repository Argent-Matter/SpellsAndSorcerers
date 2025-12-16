package dev.screret.motm.api.util.worldgen;

import dev.screret.motm.core.mixin.vanilla.StructurePlaceSettingsAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

@NoArgsConstructor
@Accessors(chain = true)
public class ExtendedStructurePlaceSettings extends StructurePlaceSettings {

    @Getter
    @Setter
    private boolean ignoreBlocks = false;
    @Setter
    private boolean runProcessors = true;

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ExtendedStructurePlaceSettings copy() {
        ExtendedStructurePlaceSettings settings = new ExtendedStructurePlaceSettings();

        // superclass's things
        settings.setMirror(this.getMirror());
        settings.setRotation(this.getRotation());
        settings.setRotationPivot(this.getRotationPivot());
        settings.setIgnoreEntities(this.isIgnoreEntities());
        settings.setBoundingBox(this.getBoundingBox());
        if (!this.shouldApplyWaterlogging()) {
            settings.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        }
        // get the actual, nullable, RandomSource from the field
        settings.setRandom(((StructurePlaceSettingsAccessor) this).motm$getRandom());
        // settings.palette = this.palette; // unused
        settings.getProcessors().addAll(this.getProcessors());
        settings.setKnownShape(this.getKnownShape());
        settings.setFinalizeEntities(this.shouldFinalizeEntities());

        // this class's own things
        settings.setIgnoreBlocks(this.isIgnoreBlocks());
        settings.setRunProcessors(this.shouldRunProcessors());

        return settings;
    }

    public static ExtendedStructurePlaceSettings fromNormalSettings(StructurePlaceSettings settings) {
        if (settings instanceof ExtendedStructurePlaceSettings extendedSettings) {
            return extendedSettings;
        }
        ExtendedStructurePlaceSettings extendedSettings = new ExtendedStructurePlaceSettings();
        extendedSettings.setMirror(settings.getMirror());
        extendedSettings.setRotation(settings.getRotation());
        extendedSettings.setRotationPivot(settings.getRotationPivot());
        extendedSettings.setIgnoreEntities(settings.isIgnoreEntities());
        extendedSettings.setBoundingBox(settings.getBoundingBox());
        if (!settings.shouldApplyWaterlogging()) {
            extendedSettings.setLiquidSettings(LiquidSettings.IGNORE_WATERLOGGING);
        }
        extendedSettings.setRandom(((StructurePlaceSettingsAccessor) settings).motm$getRandom());
        extendedSettings.getProcessors().addAll(settings.getProcessors());
        extendedSettings.setKnownShape(settings.getKnownShape());
        extendedSettings.setFinalizeEntities(settings.shouldFinalizeEntities());

        return extendedSettings;
    }

    public boolean shouldRunProcessors() {
        return this.runProcessors;
    }

    @Override
    public List<StructureProcessor> getProcessors() {
        if (!this.shouldRunProcessors()) {
            // return a mutable list, as vanilla mutates the return value (in #copy)
            return new ArrayList<>();
        }
        return super.getProcessors();
    }

    @Override
    public ExtendedStructurePlaceSettings setMirror(Mirror mirror) {
        return (ExtendedStructurePlaceSettings) super.setMirror(mirror);
    }

    @Override
    public ExtendedStructurePlaceSettings setRotation(Rotation rotation) {
        return (ExtendedStructurePlaceSettings) super.setRotation(rotation);
    }

    @Override
    public ExtendedStructurePlaceSettings setRotationPivot(BlockPos rotationPivot) {
        return (ExtendedStructurePlaceSettings) super.setRotationPivot(rotationPivot);
    }

    @Override
    public ExtendedStructurePlaceSettings setIgnoreEntities(boolean ignoreEntities) {
        return (ExtendedStructurePlaceSettings) super.setIgnoreEntities(ignoreEntities);
    }

    @Override
    public ExtendedStructurePlaceSettings setBoundingBox(BoundingBox boundingBox) {
        return (ExtendedStructurePlaceSettings) super.setBoundingBox(boundingBox);
    }

    @Override
    public ExtendedStructurePlaceSettings setRandom(@Nullable RandomSource random) {
        return (ExtendedStructurePlaceSettings) super.setRandom(random);
    }

    public ExtendedStructurePlaceSettings setLiquidSettings(LiquidSettings liquidSettings) {
        return (ExtendedStructurePlaceSettings) super.setLiquidSettings(liquidSettings);
    }

    public ExtendedStructurePlaceSettings setKnownShape(boolean knownShape) {
        return (ExtendedStructurePlaceSettings) super.setKnownShape(knownShape);
    }

    public ExtendedStructurePlaceSettings clearProcessors() {
        return (ExtendedStructurePlaceSettings) super.clearProcessors();
    }

    public ExtendedStructurePlaceSettings addProcessor(StructureProcessor processor) {
        return (ExtendedStructurePlaceSettings) super.addProcessor(processor);
    }

    public ExtendedStructurePlaceSettings popProcessor(StructureProcessor processor) {
        return (ExtendedStructurePlaceSettings) super.popProcessor(processor);
    }
}
