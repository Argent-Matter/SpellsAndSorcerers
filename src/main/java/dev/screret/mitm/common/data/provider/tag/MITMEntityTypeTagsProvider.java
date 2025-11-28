package dev.screret.mitm.common.data.provider.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.data.MITMEntityTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MITMEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public MITMEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                      @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registries, MagicOfTheMind.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider registries) {
        this.tag(EntityTypeTags.ILLAGER).add(MITMEntityTypes.WIZARD.get()).add(MITMEntityTypes.BOSS_WIZARD.get());
    }
}
