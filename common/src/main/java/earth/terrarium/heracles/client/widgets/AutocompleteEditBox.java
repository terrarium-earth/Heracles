package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class AutocompleteEditBox<T> extends EditBox {

    private final List<T> suggestions = new ArrayList<>();
    private final List<String> filteredSuggestions = new ArrayList<>();

    private final BiPredicate<String, T> filter;
    private final Function<T, String> mapper;

    private final Consumer<String> onEnter;

    public AutocompleteEditBox(Font font, int x, int y, int width, int height, BiPredicate<String, T> filter, Function<T, String> mapper, Consumer<String> onEnter) {
        super(font, x, y, width, height, CommonComponents.EMPTY);
        this.filter = filter;
        this.mapper = mapper;
        this.onEnter = onEnter;
        setResponder(value -> filter());
    }

    @Override
    public void renderWidget(PoseStack poseStack, int i, int j, float f) {
        super.renderWidget(poseStack, i, j, f);
        if (!isFocused()) return;
        int x = this.getX();
        int y = this.getY() + this.getHeight() + 1;
        int width = this.getWidth();
        if (!filteredSuggestions.isEmpty()) {
            int height = 10 * filteredSuggestions.size();
            fill(poseStack, x, y, x + width, y + height, 0x80000000);
            renderOutline(poseStack, x - 1, y - 1, width + 2, height + 2, this.isFocused() ? 0xffffffff : 0xffa0a0a0);
            for (int k = 0; k < filteredSuggestions.size(); k++) {
                String suggestion = filteredSuggestions.get(k);
                Minecraft.getInstance().font.draw(poseStack, suggestion, x + 4, y + 1 + (k * 10), 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY) && mouseY > (this.getY() + this.getHeight() + 1)) {
            if (!filteredSuggestions.isEmpty()) {
                String suggestion = filteredSuggestions.get((int) ((mouseY - (this.getY() + this.getHeight() + 1)) / 10));
                this.setValue(suggestion);
                this.moveCursorToEnd();
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
}
