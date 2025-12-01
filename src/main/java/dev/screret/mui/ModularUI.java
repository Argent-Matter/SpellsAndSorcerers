package dev.screret.mui;

import net.minecraft.resources.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModularUI {

    public static final String MOD_ID = "modularui";
    public static final String NAME = "Modular UI";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static final ResourceLocation TEMPLATE_LOCATION = ResourceLocation.fromNamespaceAndPath(MOD_ID, "");

    public ModularUI() {

    }

    public static ResourceLocation id(String path) {
        return TEMPLATE_LOCATION.withPath(path);
    }
}
