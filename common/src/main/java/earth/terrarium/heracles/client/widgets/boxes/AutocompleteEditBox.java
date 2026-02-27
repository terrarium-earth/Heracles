package earth.terrarium.heracles.client.widgets.boxes;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import earth.terrarium.heracles.client.components.widgets.textbox.TextBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class AutocompleteEditBox<T> extends TextBox {

    private final List<T> suggestions = new ArrayList<>();
    private final List<String> filteredSuggestions = new ArrayList<>();

    private final BiPredicate<String, T> filter;
    private final Function<T, String> mapper;

    private final Consumer<String> onEnter;

    @Nullable
    private Consumer<String> responder;

    public AutocompleteEditBox(TextBox box, String value, int width, int height, BiPredicate<String, T> filter, Function<T, String> mapper, Consumer<String> onEnter) {
        super(box, value, width, height, Short.MAX_VALUE);
        this.filter = filter;
        this.mapper = mapper;
        this.onEnter = onEnter;
    }

    public void setResponder(@Nullable Consumer<String> responder) {
        this.responder = responder;
    }

    @Override
    protected void onValueChange() {
        if (this.responder != null) {
            this.responder.accept(this.getValue());
        }
        filter();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        super.renderWidget(graphics, i, j, f);
        if (!isFocused()) return;
        int x = this.getX();
        int y = this.getY() + this.getHeight() + 1;
        int width = this.getWidth();
        if (!filteredSuggestions.isEmpty()) {
            int height = 10 * filteredSuggestions.size();
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(0, 0, 100);
                graphics.fill(x, y, x + width, y + height, 0x80000000);
                graphics.renderOutline(x - 1, y - 1, width + 2, height + 2, this.isFocused() ? 0xffffffff : 0xffa0a0a0);
                for (int k = 0; k < filteredSuggestions.size(); k++) {
                    String suggestion = filteredSuggestions.get(k);
                    graphics.drawString(
                        Minecraft.getInstance().font,
                        suggestion, x + 4, y + 1 + (k * 10), 0xFFFFFFFF,
                        false
                    );
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHoveredOrFocused() && button == 0 && isMouseOver(mouseX, mouseY) && mouseY > (this.getY() + this.getHeight() + 1)) {
            if (!filteredSuggestions.isEmpty()) {
                String suggestion = filteredSuggestions.get((int) ((mouseY - (this.getY() + this.getHeight() + 1)) / 10));
                this.setValue(suggestion);
                this.moveCursorTo(this.getValue().length());
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }
        return this.visible
            && mouseX >= (double) this.getX()
            && mouseX < (double) (this.getX() + this.width)
            && mouseY >= (double) this.getY() + this.height + 1
            && mouseY < (double) (this.getY() + this.height + 1 + (10 * filteredSuggestions.size()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == InputConstants.KEY_RETURN) {
            onEnter.accept(this.getValue());
            this.setValue("");
            return true;
        }
        return false;
    }

    public void setSuggestions(Collection<T> suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(suggestions);
        filter();
    }

    public void filter() {
        this.filteredSuggestions.clear();
        String text = this.getValue();
        if (text.isEmpty()) {
            return;
        }
        for (T suggestion : suggestions) {
            if (filter.test(text, suggestion)) {
                this.filteredSuggestions.add(mapper.apply(suggestion));
            }
        }
    }

    @Nullable
    public T value() {
        String text = this.getValue();
        for (T suggestion : suggestions) {
            if (mapper.apply(suggestion).equals(text)) {
                return suggestion;
            }
        }
        return null;
    }
}
