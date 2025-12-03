package dev.screret.motm.common.ui;

import dev.screret.modularui.drawable.ColorType;
import dev.screret.modularui.drawable.UITexture;
import dev.screret.motm.MagicOfTheMind;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface MOTMGuiTextures {

    // spotless:off

    // ICONS

    // BACKGROUNDS

    // BUTTONS

    // BUTTON OVERLAYS

    // PROGRESS BARS

    // MISC


    // spotless:on

    private static UITexture fullImage(String path) {
        return fullImage(path, null);
    }

    private static UITexture fullImage(String path, ColorType colorType) {
        return UITexture.fullImage(MagicOfTheMind.MODID, path, colorType);
    }

    @SuppressWarnings("SameParameterValue")
    private static UITexture[] slice(String path, int imageWidth, int imageHeight, int sliceWidth, int sliceHeight,
                                     ColorType colorType) {
        if (imageWidth % sliceWidth != 0 || imageHeight % sliceHeight != 0)
            throw new IllegalArgumentException("Slice height and slice width must divide the image evenly!");

        int countX = imageWidth / sliceWidth;
        int countY = imageHeight / sliceHeight;
        UITexture[] slices = new UITexture[countX * countY];

        for (int indexX = 0; indexX < countX; indexX++) {
            for (int indexY = 0; indexY < countY; indexY++) {
                slices[(indexX * countX) + indexY] = UITexture.builder()
                        .location(MagicOfTheMind.MODID, path)
                        .imageSize(imageWidth, imageHeight)
                        .colorType(colorType)
                        .subAreaXYWH(indexX * sliceWidth, indexY * sliceHeight, sliceWidth, sliceHeight)
                        .build();
            }
        }
        return slices;
    }

    private static UITexture progressBar(String path) {
        return progressBar(path, null);
    }

    private static UITexture progressBar(String path, ColorType colorType) {
        return progressBar(path, 20, 40, colorType);
    }

    private static UITexture progressBar(String path, int width, int height) {
        return progressBar(path, width, height, null);
    }

    private static UITexture progressBar(String path, int width, int height, ColorType colorType) {
        UITexture.Builder builder = new UITexture.Builder()
                .location(MagicOfTheMind.MODID, path)
                .imageSize(width, height)
                .colorType(colorType);
        return builder.build();
    }
}
