package dev.screret.modularui.drawable;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.api.GuiAxis;

import static dev.screret.modularui.drawable.UITexture.fullImageIcon;

public interface GuiTextures {

    UITexture GEAR = fullImageIcon("gui/icon/gear");
    UITexture MORE = fullImageIcon("gui/icon/more");
    UITexture SAVED = fullImageIcon("gui/icon/saved");
    UITexture SAVE = fullImageIcon("gui/icon/save");
    UITexture ADD = fullImageIcon("gui/icon/add");
    UITexture DUPE = fullImageIcon("gui/icon/dupe");
    UITexture REMOVE = fullImageIcon("gui/icon/remove");
    UITexture POSE = fullImageIcon("gui/icon/pose");
    UITexture FILTER = fullImageIcon("gui/icon/filter");
    UITexture MOVE_UP = fullImageIcon("gui/icon/move_up");
    UITexture MOVE_DOWN = fullImageIcon("gui/icon/move_down");
    UITexture LOCKED = fullImageIcon("gui/icon/locked");
    UITexture UNLOCKED = fullImageIcon("gui/icon/unlocked");
    UITexture COPY = fullImageIcon("gui/icon/copy");
    UITexture PASTE = fullImageIcon("gui/icon/paste");
    UITexture CUT = fullImageIcon("gui/icon/cut");
    UITexture REFRESH = fullImageIcon("gui/icon/refresh");

    UITexture DOWNLOAD = fullImageIcon("gui/icon/download");
    UITexture UPLOAD = fullImageIcon("gui/icon/upload");
    UITexture SERVER = fullImageIcon("gui/icon/server");
    UITexture FOLDER = fullImageIcon("gui/icon/folder");
    UITexture IMAGE = fullImageIcon("gui/icon/image");
    UITexture EDIT = fullImageIcon("gui/icon/edit");
    UITexture MATERIAL = fullImageIcon("gui/icon/material");
    UITexture CLOSE = fullImageIcon("gui/icon/close");
    UITexture LIMB = fullImageIcon("gui/icon/limb");
    UITexture CODE = fullImageIcon("gui/icon/code");
    UITexture MOVE_LEFT = fullImageIcon("gui/icon/move_left");
    UITexture MOVE_RIGHT = fullImageIcon("gui/icon/move_right");
    UITexture HELP = fullImageIcon("gui/icon/help");
    UITexture LEFT_HANDLE = fullImageIcon("gui/icon/left_handle");
    UITexture MAIN_HANDLE = fullImageIcon("gui/icon/main_handle");
    UITexture RIGHT_HANDLE = fullImageIcon("gui/icon/right_handle");
    UITexture REVERSE = fullImageIcon("gui/icon/reverse");
    UITexture BLOCK = fullImageIcon("gui/icon/block");

    UITexture FAVORITE = fullImageIcon("gui/icon/favorite");
    UITexture VISIBLE = fullImageIcon("gui/icon/visible");
    UITexture INVISIBLE = fullImageIcon("gui/icon/invisible");
    UITexture PLAY = fullImageIcon("gui/icon/play");
    UITexture PAUSE = fullImageIcon("gui/icon/pause");
    UITexture MAXIMIZE = fullImageIcon("gui/icon/maximize");
    UITexture MINIMIZE = fullImageIcon("gui/icon/minimize");
    UITexture STOP = fullImageIcon("gui/icon/stop");
    UITexture FULLSCREEN = fullImageIcon("gui/icon/fullscreen");
    UITexture ALL_DIRECTIONS = fullImageIcon("gui/icon/all_directions");
    UITexture SPHERE = fullImageIcon("gui/icon/sphere");
    UITexture SHIFT_TO = fullImageIcon("gui/icon/shift_to");
    UITexture SHIFT_FORWARD = fullImageIcon("gui/icon/shift_forward");
    UITexture SHIFT_BACKWARD = fullImageIcon("gui/icon/shift_backward");
    UITexture MOVE_TO = fullImageIcon("gui/icon/move_to");
    UITexture GRAPH = fullImageIcon("gui/icon/graph");

    UITexture WRENCH = fullImageIcon("gui/icon/wrench");
    UITexture EXCLAMATION = fullImageIcon("gui/icon/exclamation");
    UITexture LEFTLOAD = fullImageIcon("gui/icon/leftload");
    UITexture RIGHTLOAD = fullImageIcon("gui/icon/rightload");
    UITexture BUBBLE = fullImageIcon("gui/icon/bubble");
    UITexture FILE = fullImageIcon("gui/icon/file");
    UITexture PROCESSOR = fullImageIcon("gui/icon/processor");
    UITexture MAZE = fullImageIcon("gui/icon/maze");
    UITexture BOOKMARK = fullImageIcon("gui/icon/bookmark");
    UITexture SOUND = fullImageIcon("gui/icon/sound");
    UITexture SEARCH = fullImageIcon("gui/icon/search");

    UITexture CHECKBOARD = fullImageIcon("gui/icon/checkboard");
    UITexture DISABLED = fullImageIcon("gui/icon/disabled");
    UITexture CURSOR = fullImageIcon("gui/icon/cursor");

    UITexture MUI_LOGO = UITexture.builder()
            .location(ModularUI.MOD_ID, "modular_ui_logo")
            .imageSize(603, 603)
            .name("logo")
            .build();

    UITexture MC_BACKGROUND = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/background/vanilla_background")
            .imageSize(195, 136)
            .adaptable(4)
            .name("vanilla_background")
            .defaultColorType()
            .build();

    UITexture MENU_BACKGROUND = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/background/menu")
            .imageSize(18, 18)
            .adaptable(1)
            .name("menu")
            .defaultColorType()
            .build();

    UITexture MC_BUTTON = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/mc_button")
            .imageSize(16, 32) // texture is 32x64, but this looks nicer
            .subAreaUV(0f, 0f, 1f, 0.5f)
            .adaptable(2).tiled()
            .name("mc_button")
            .defaultColorType()
            .build();

    UITexture MC_BUTTON_PRESSED = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/mc_button")
            .imageSize(16, 32)
            .subAreaUV(0f, 0.5f, 1f, 1f)
            .adaptable(2).tiled()
            .name("mc_button_pressed")
            .defaultColorType()
            .build();

    UITexture MC_BUTTON_HOVERED = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/mc_button_hovered")
            .imageSize(16, 32)
            .subAreaUV(0f, 0f, 1f, 0.5f)
            .adaptable(2).tiled()
            .name("mc_button_hovered")
            .build();

    UITexture MC_BUTTON_HOVERED_PRESSED = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/mc_button_hovered")
            .imageSize(16, 32)
            .subAreaUV(0f, 0.5f, 1f, 1f)
            .adaptable(2).tiled()
            .name("mc_button_hovered_pressed")
            .build();

    UITexture MC_BUTTON_DISABLED = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/mc_button_disabled")
            .imageSize(16, 16)
            .fullImage()
            .adaptable(1).tiled()
            .name("mc_button_disabled")
            .defaultColorType()
            .build();

    UITexture BUTTON_CLEAN = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/base_button")
            .imageSize(18, 18)
            .adaptable(1)
            .name("vanilla_button").canApplyTheme()
            .build();

    UITexture DISPLAY = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/background/display")
            .imageSize(143, 75)
            .adaptable(2)
            .name("display")
            .build();

    UITexture DISPLAY_SMALL = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/background/display_small")
            .imageSize(18, 18)
            .adaptable(1)
            .name("display_small")
            .build();

    UITexture SLOT_ITEM = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/slot/item")
            .imageSize(18, 18)
            .adaptable(1)
            .canApplyTheme()
            .name("slot_item")
            .build();

    UITexture SLOT_FLUID = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/slot/fluid")
            .imageSize(18, 18)
            .adaptable(1)
            .canApplyTheme()
            .name("slot_fluid")
            .build();

    UITexture PROGRESS_ARROW = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/progress_bar_arrow")
            .imageSize(20, 40)
            .canApplyTheme()
            .build();

    UITexture PROGRESS_CYCLE = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/progress_bar_mixer")
            .imageSize(20, 40)
            .canApplyTheme()
            .build();

    UITexture CYCLE_BUTTON_DEMO = UITexture.builder()
            .location(ModularUI.MOD_ID, "gui/widgets/cycle_button_demo")
            .imageSize(18, 54)
            .build();

    UITexture CHECK_BOX = fullImageIcon("gui/widgets/toggle_config");
    UITexture CROSS = fullImageIcon("gui/icons/cross");
    UITexture CROSS_TINY = fullImageIcon("gui/icons/cross_tiny");
    UITexture CHECK_BOX_EMPTY = CHECK_BOX.getSubArea(0, 0, 1f, 0.5f);
    UITexture CHECK_BOX_FULL = CHECK_BOX.getSubArea(0, 0.5f, 1f, 1f);

    TabTexture TAB_TOP = TabTexture.of(fullImageIcon("gui/tab/tabs_top", ColorType.DEFAULT), GuiAxis.Y,
            false, 28, 32, 4);
    TabTexture TAB_BOTTOM = TabTexture.of(fullImageIcon("gui/tab/tabs_bottom", ColorType.DEFAULT),
            GuiAxis.Y, true, 28, 32, 4);
    TabTexture TAB_LEFT = TabTexture.of(fullImageIcon("gui/tab/tabs_left", ColorType.DEFAULT), GuiAxis.X,
            false, 32, 28, 4);
    TabTexture TAB_RIGHT = TabTexture.of(fullImageIcon("gui/tab/tabs_right", ColorType.DEFAULT),
            GuiAxis.X, true, 32, 28, 4);
}
