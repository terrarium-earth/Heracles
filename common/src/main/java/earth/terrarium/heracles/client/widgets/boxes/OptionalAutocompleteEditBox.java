package earth.terrarium.heracles.client.widgets.boxes;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class OptionalAutocompleteEditBox<T> extends AutocompleteEditBox<T> {
    private static final int CHECKBOX_WIDTH = 24;
    private final Checkbox checkbox;

    public OptionalAutocompleteEditBox(Font font, int x, int y, int width, int height, BiPredicate<String, T> filter, Function<T, String> mapper, Consumer<String> onEnter) {
        super(font, x + CHECKBOX_WIDTH, y, width - CHECKBOX_WIDTH, height, filter, mapper, onEnter);
        this.checkbox = new Checkbox(x, y, CHECKBOX_WIDTH, height, CommonComponents.EMPTY, false, false);
    }

    public @Nullable T nullableValue() {
        return checkbox.selected() ? this.value() : null;
    }

    public void setValue(@Nullable String value) {
        if (value == null) {
            if (checkbox.selected()) checkbox.onPress();
        } else {
            if (!checkbox.selected()) checkbox.onPress();
            super.setValue(value);
        }
        this.updateEditBoxEditability();
    }

    private void updateEditBoxEditability() {
        this.setEditable(checkbox.selected());
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.checkbox.render(graphics, mouseX, mouseY, partialTick);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.checkbox.mouseClicked(mouseX, mouseY, button) | super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || this.checkbox.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.checkbox.keyPressed(keyCode, scanCode, modifiers) | super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return this.checkbox.charTyped(codePoint, modifiers) | super.charTyped(codePoint, modifiers);
    }

    @Override
    public void setX(int x) {
        this.checkbox.setX(x);
        super.setX(x + CHECKBOX_WIDTH);
    }

    @Override
    public void setY(int y) {
        this.checkbox.setY(y);
        super.setY(y);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width - CHECKBOX_WIDTH);
    }
}
