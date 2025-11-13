package earth.terrarium.heracles.client.widgets.boxes;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class OptionalAutocompleteEditBox<T> extends LinearLayout implements Renderable, GuiEventListener {
    private static final int CHECKBOX_WIDTH = 24;
    private final Checkbox checkbox;
    private final AutocompleteEditBox<T> editBox;

    public OptionalAutocompleteEditBox(Font font, int x, int y, int width, int height, BiPredicate<String, T> filter, Function<T, String> mapper, Consumer<String> onEnter) {
        super(x, y, width, height, Orientation.HORIZONTAL);
        this.checkbox = new Checkbox(x, y, CHECKBOX_WIDTH, height, CommonComponents.EMPTY, false, false);
        this.editBox = new AutocompleteEditBox<>(font, x + CHECKBOX_WIDTH, y, width - CHECKBOX_WIDTH, height, filter, mapper, onEnter);
        this.addChild(this.checkbox);
        this.addChild(this.editBox);
    }

    public @Nullable String value() {
        return checkbox.selected() ? editBox.getValue() : null;
    }

    public void setValue(@Nullable String value) {
        if (value == null) {
            if (checkbox.selected()) checkbox.onPress();
        } else {
            if (!checkbox.selected()) checkbox.onPress();
            this.editBox.setValue(value);
        }
    }

    public AutocompleteEditBox<T> getEditBox() {
        return this.editBox;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.checkbox.render(guiGraphics, mouseX, mouseY, partialTick);
        this.editBox.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.checkbox.mouseMoved(mouseX, mouseY);
        this.editBox.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.checkbox.mouseClicked(mouseX, mouseY, button) | this.editBox.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.checkbox.mouseReleased(mouseX, mouseY, button) | this.editBox.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.checkbox.mouseDragged(mouseX, mouseY, button, dragX, dragY) | this.editBox.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.checkbox.mouseScrolled(mouseX, mouseY, delta) | this.editBox.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.checkbox.keyPressed(keyCode, scanCode, modifiers) | this.checkbox.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.checkbox.keyReleased(keyCode, scanCode, modifiers) | this.checkbox.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.checkbox.charTyped(codePoint, modifiers) | this.editBox.charTyped(codePoint, modifiers);
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return this.editBox.nextFocusPath(event);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.checkbox.isMouseOver(mouseX, mouseY) | this.editBox.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean focused) {
        this.editBox.setFocused(focused);
    }

    @Override
    public boolean isFocused() {
        return this.editBox.isFocused();
    }

    @Override
    public @Nullable ComponentPath getCurrentFocusPath() {
        return this.editBox.getCurrentFocusPath();
    }

    @Override
    public @NotNull ScreenRectangle getRectangle() {
        return super.getRectangle();
    }
}
