package earth.terrarium.heracles.client.components.widgets.textbox.autocomplete;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.ListWidget;
import earth.terrarium.heracles.client.components.widgets.textbox.TextBox;
import earth.terrarium.heracles.client.ui.Overlay;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class AutocompleteScreen<T> extends Overlay {

    private static final ResourceLocation LIST = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/textbox/list.png");
    private static final int ENTRY_HEIGHT = 12;

    private final List<T> suggestions = new ArrayList<>();
    private final List<ListWidget.Item> filteredSuggestions = new ArrayList<>();

    private final BiPredicate<String, T> filter;
    private final Function<T, String> mapper;

    private final AutocompleteTextBox<T> widget;

    private ListWidget options;
    private TextBox textBox;

    protected AutocompleteScreen(Screen background, AutocompleteTextBox<T> widget) {
        super(background);
        this.filter = widget.filter;
        this.mapper = widget.mapper;
        this.suggestions.addAll(widget.suggestions);
        this.widget = widget;
    }

    public int x() {
        return this.widget.getX();
    }

    public int y() {
        int y = this.widget.getY() - this.height();
        if (y < 0) {
            y = this.widget.getY() + this.widget.getHeight();
        }
        return y;
    }

    public int width() {
        return this.widget.getWidth();
    }

    public int height() {
        return Math.min(ENTRY_HEIGHT * 10, this.filteredSuggestions.size() * ENTRY_HEIGHT) + 3;
    }

    @Override
    protected void init() {
        this.textBox = addRenderableWidget(new TextBox(
            this.textBox, this.widget.value,
            this.widget.getWidth(), this.widget.getHeight(), Short.MAX_VALUE,
            s -> true, this::filter
        ));
        this.textBox.setPosition(this.widget.getX(), this.widget.getY());

        ListWidget old = this.options;
        this.options = addRenderableWidget(new ListWidget(this.width() - 3, this.height() - 3));
        this.options.setPosition(this.x() + 1, this.y() + 2);
        this.options.update(old);

        setFocused(this.textBox);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        this.onClose();
        return true;
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        if (this.filteredSuggestions.isEmpty()) return;
        UIUtils.blitWithEdge(graphics, LIST, this.x(), this.y(), this.width(), this.height(), 3);
    }

    public T value() {
        for (T suggestion : this.suggestions) {
            if (this.mapper.apply(suggestion).equals(text())) {
                return suggestion;
            }
        }
        return null;
    }

    public String text() {
        return this.textBox.getValue();
    }

    public void filter(String text) {
        this.widget.value = text;

        this.filteredSuggestions.clear();
        if (!text.isEmpty()) {
            for (T suggestion : this.suggestions) {
                if (this.filter.test(text, suggestion)) {
                    String value = this.mapper.apply(suggestion);
                    this.filteredSuggestions.add(new AutocompleteEntry<>(
                        this.width() - 3, ENTRY_HEIGHT,
                        value, () -> {
                            this.textBox.setValue(value);
                            this.onClose();
                        }
                    ));
                }
            }
        }

        this.options.set(this.filteredSuggestions);
        this.options.setHeight(this.height() - 3);
        this.options.setPosition(this.x() + 1, this.y() + 2);
    }
}
