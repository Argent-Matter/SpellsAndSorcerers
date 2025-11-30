package dev.screret.motm.common.data.provider.tag;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.data.MOTMEntityTypes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MOTMEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public MOTMEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                      @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registries, MagicOfTheMind.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider registries) {
        this.tag(EntityTypeTags.ILLAGER).add(MOTMEntityTypes.WIZARD.get()).add(MOTMEntityTypes.BOSS_WIZARD.get());
    }
}
