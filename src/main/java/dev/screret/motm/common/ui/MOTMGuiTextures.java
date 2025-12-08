package dev.screret.motm.common.ui;

import dev.screret.modularui.drawable.ColorType;
import dev.screret.modularui.drawable.GuiTextures;
import dev.screret.modularui.drawable.UITexture;
import dev.screret.motm.MagicOfTheMind;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface MOTMGuiTextures {

    // spotless:off

    // ICONS

    // BACKGROUNDS
    UITexture BACKGROUND = GuiTextures.MC_BACKGROUND;

    // BUTTONS

    // BUTTON OVERLAYS

    // PROGRESS BARS
    UITexture PROGRESS_BAR_ARROW = progressBar("gui/progress_bar/progress_bar_arrow");
    UITexture PROGRESS_BAR_ARROW_MULTIPLE = progressBar("gui/progress_bar/progress_bar_arrow_multiple");
    UITexture PROGRESS_BAR_ARROW_VANILLA = progressBar("gui/progress_bar/progress_bar_arrow_vanilla", 22, 40);
    UITexture PROGRESS_BAR_CRYSTALLIZATION = progressBar("gui/progress_bar/progress_bar_crystallization");
    UITexture PROGRESS_BAR_EXTRACT = progressBar("gui/progress_bar/progress_bar_extract");
    UITexture PROGRESS_BAR_FUSION = progressBar("gui/progress_bar/progress_bar_fusion");
    UITexture PROGRESS_BAR_MASS_FAB = progressBar("gui/progress_bar/progress_bar_mass_fab");
    UITexture PROGRESS_BAR_MIXER = progressBar("gui/progress_bar/progress_bar_mixer");
    UITexture PROGRESS_BAR_REPLICATOR = progressBar("gui/progress_bar/progress_bar_replicator");
    UITexture PROGRESS_BAR_SIFT = progressBar("gui/progress_bar/progress_bar_sift");
    UITexture PROGRESS_BAR_SLICE = progressBar("gui/progress_bar/progress_bar_slice");

    // MISC
    UITexture MANA_BAR = progressBar("gui/overlay/mana_bar", 80, 10);

    // spotless:on

    private static UITexture fullImage(String path) {
        return fullImage(path, null);
    }

    private static UITexture fullImage(String path, ColorType colorType) {
        return UITexture.fullImage(MagicOfTheMind.MOD_ID, path, colorType);
    }

    @SuppressWarnings("SameParameterValue")
    private static UITexture[] slice(String path, int imageWidth, int imageHeight, int sliceWidth, int sliceHeight,
                                     ColorType colorType) {
        if (imageWidth % sliceWidth != 0 || imageHeight % sliceHeight != 0) {
            throw new IllegalArgumentException("Slice height and slice width must divide the image evenly!");
        }

        int countX = imageWidth / sliceWidth;
        int countY = imageHeight / sliceHeight;
        UITexture[] slices = new UITexture[countX * countY];

        for (int indexX = 0; indexX < countX; indexX++) {
            for (int indexY = 0; indexY < countY; indexY++) {
                slices[(indexX * countX) + indexY] = UITexture.builder()
                        .location(MagicOfTheMind.MOD_ID, path)
                        .imageSize(imageWidth, imageHeight)
                        .colorType(colorType)
                        .subAreaXYWH(indexX * sliceWidth, indexY * sliceHeight, sliceWidth, sliceHeight)
                        .build();
            }
        }
        return slices;
    }

    private static UITexture progressBar(String path) {
        return progressBar(path, ColorType.DEFAULT);
    }

    private static UITexture progressBar(String path, @Nullable ColorType colorType) {
        return progressBar(path, 20, 40, colorType);
    }

    private static UITexture progressBar(String path, int width, int height) {
        return progressBar(path, width, height, ColorType.DEFAULT);
    }

    private static UITexture progressBar(String path, int width, int height, @Nullable ColorType colorType) {
        UITexture.Builder builder = new UITexture.Builder()
                .location(MagicOfTheMind.MOD_ID, path)
                .imageSize(width, height)
                .colorType(colorType);
        return builder.build();
    }
}
