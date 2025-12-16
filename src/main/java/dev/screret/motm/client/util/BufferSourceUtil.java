package dev.screret.motm.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;

public class BufferSourceUtil {

    public static MultiBufferSource.BufferSource getRealBufferSource(MultiBufferSource bufferSource) {
        if (bufferSource instanceof MultiBufferSource.BufferSource multi) {
            return multi;
        } else {
            return Minecraft.getInstance().levelRenderer.renderBuffers.bufferSource();
        }
    }
}
