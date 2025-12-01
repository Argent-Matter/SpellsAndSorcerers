package dev.screret.mui.client;

import dev.screret.mui.api.widget.ResizeDragArea;

import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class CursorHandler {

    public enum CursorIcon {

        DEFAULT,
        TEXT,
        POINTER,
        NOT_ALLOWED,
        CROSSHAIR,
        RESIZE_HORIZONTAL,
        RESIZE_VERTICAL,
        RESIZE_TL_BR,
        RESIZE_TR_BL,
        RESIZE_ALL,
    }

    // the normal pointer cursor
    private static long CURSOR_NORMAL;
    // text input cursor
    // usually I-beam shaped
    private static long CURSOR_TEXT_INPUT;
    // "hovering over a clickable object" cursor
    // usually a pointing finger
    private static long CURSOR_POINT_HOVERED;
    // crosshair cursor
    private static long CURSOR_CROSSHAIR;
    // "operation not allowed" cursor
    // usually a circle with a line through it
    private static long CURSOR_NOT_ALLOWED;

    // left to right resize cursor
    private static long CURSOR_RESIZE_HORIZONTAL;
    // top to down resize cursor
    private static long CURSOR_RESIZE_VERTICAL;
    // top right to bottom left resize cursor
    private static long CURSOR_RESIZE_TR_BL;
    // top-left to bottom right resize cursor
    private static long CURSOR_RESIZE_TL_BR;
    // omnidirectional resize cursor
    // has arrows up-down and left-right
    private static long CURSOR_RESIZE_ALL;

    public static void setCursorResizeIcon(@Nullable ResizeDragArea dragArea) {
        if (dragArea == null) {
            resetCursorIcon();
            return;
        }
        CursorIcon icon = switch (dragArea) {
            case TOP_LEFT, BOTTOM_RIGHT -> CursorIcon.RESIZE_TL_BR;
            case TOP_RIGHT, BOTTOM_LEFT -> CursorIcon.RESIZE_TR_BL;
            case TOP, BOTTOM -> CursorIcon.RESIZE_VERTICAL;
            case RIGHT, LEFT -> CursorIcon.RESIZE_HORIZONTAL;
        };
        setCursorIcon(icon);
    }

    public static void setCursorIcon(CursorIcon cursorIcon) {
        long icon = switch (cursorIcon) {
            case DEFAULT -> CURSOR_NORMAL;
            case TEXT -> CURSOR_TEXT_INPUT;
            case POINTER -> CURSOR_POINT_HOVERED;
            case CROSSHAIR -> CURSOR_CROSSHAIR;
            case RESIZE_HORIZONTAL -> CURSOR_RESIZE_HORIZONTAL;
            case RESIZE_VERTICAL -> CURSOR_RESIZE_VERTICAL;
            case RESIZE_TL_BR -> CURSOR_RESIZE_TL_BR;
            case RESIZE_TR_BL -> CURSOR_RESIZE_TR_BL;
            case RESIZE_ALL -> CURSOR_RESIZE_ALL;
            case NOT_ALLOWED -> CURSOR_NOT_ALLOWED;
        };
        GLFW.glfwSetCursor(Minecraft.getInstance().getWindow().getWindow(), icon);
    }

    public static void resetCursorIcon() {
        setCursorIcon(CursorIcon.DEFAULT);
    }

    @ApiStatus.Internal
    public static void init() {
        // load platform-specific default cursors (instead of using custom textures)

        // GLFW will switch to the default cursor when 0 is passed into glfwSetCursor
        CURSOR_NORMAL = 0; // GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        CURSOR_TEXT_INPUT = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
        CURSOR_CROSSHAIR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
        CURSOR_POINT_HOVERED = GLFW.glfwCreateStandardCursor(GLFW.GLFW_POINTING_HAND_CURSOR);
        CURSOR_NOT_ALLOWED = GLFW.glfwCreateStandardCursor(GLFW.GLFW_NOT_ALLOWED_CURSOR);

        CURSOR_RESIZE_HORIZONTAL = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_EW_CURSOR);
        CURSOR_RESIZE_VERTICAL = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NS_CURSOR);
        CURSOR_RESIZE_TR_BL = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR);
        CURSOR_RESIZE_TL_BR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR);
        CURSOR_RESIZE_ALL = GLFW.glfwCreateStandardCursor(GLFW.GLFW_RESIZE_ALL_CURSOR);
    }
}
