package dev.screret.mui.drawable;

import dev.screret.mui.api.drawable.IIcon;
import dev.screret.mui.api.widget.IGuiAction;
import dev.screret.mui.api.widget.Interactable;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true, chain = true)
public class InteractableIcon extends DelegateIcon implements Interactable {

    @Setter
    private IGuiAction.MousePressed onMousePressed;
    @Setter
    private IGuiAction.MouseReleased onMouseReleased;
    @Setter
    private IGuiAction.MousePressed onMouseTapped;
    @Setter
    private IGuiAction.MouseScroll onMouseScrolled;
    @Setter
    private IGuiAction.KeyPressed onKeyPressed;
    @Setter
    private IGuiAction.KeyReleased onKeyReleased;
    @Setter
    private IGuiAction.KeyPressed onKeyTapped;

    public InteractableIcon(IIcon icon) {
        super(icon);
    }

    public void playClickSound() {
        // if (this.playClickSound) {
        Interactable.playButtonClickSound();
        // }
    }

    @Override
    public @NotNull Result onMousePressed(double mouseX, double mouseY, int button) {
        if (this.onMousePressed != null && this.onMousePressed.press(mouseX, mouseY, button)) {
            playClickSound();
            return Result.SUCCESS;
        }
        return Result.ACCEPT;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return this.onMouseReleased != null && this.onMouseReleased.release(mouseX, mouseY, button);
    }

    @NotNull
    @Override
    public Result onMouseTapped(double mouseX, double mouseY, int button) {
        if (this.onMouseTapped != null && this.onMouseTapped.press(mouseX, mouseY, button)) {
            playClickSound();
            return Result.SUCCESS;
        }
        return Result.IGNORE;
    }

    @Override
    public @NotNull Result onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.onKeyPressed != null && this.onKeyPressed.press(keyCode, scanCode, modifiers)) {
            return Result.SUCCESS;
        }
        return Result.ACCEPT;
    }

    @Override
    public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
        return this.onKeyReleased != null && this.onKeyReleased.release(keyCode, scanCode, modifiers);
    }

    @NotNull
    @Override
    public Result onKeyTapped(int keyCode, int scanCode, int modifiers) {
        if (this.onKeyTapped != null && this.onKeyTapped.press(keyCode, scanCode, modifiers)) {
            return Result.SUCCESS;
        }
        return Result.IGNORE;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.onMouseScrolled != null && this.onMouseScrolled.scroll(mouseX, mouseY, scrollX, scrollY);
    }
}
