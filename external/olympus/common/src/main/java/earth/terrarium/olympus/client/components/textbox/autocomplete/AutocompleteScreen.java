package earth.terrarium.olympus.client.components.textbox.autocomplete;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.olympus.client.components.base.ListWidget;
import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.ui.Overlay;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteScreen<T> extends Overlay {

    private static final Identifier LIST = UIConstants.id("lists/background");
    private static final int ENTRY_HEIGHT = 12;

    private final List<AbstractWidget> filteredSuggestions = new ArrayList<>();
    protected final ListenableState<String> text;

    protected final AutocompleteTextBox<T> widget;

    protected ListWidget options;
    protected TextBox textBox;

    protected AutocompleteScreen(AutocompleteTextBox<T> widget) {
        super(Minecraft.getInstance().screen);
        this.widget = widget;
        this.text = ListenableState.of(widget.state());
        this.text.registerListener(this::filter);
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
        this.textBox = addRenderableWidget(new TextBox(this.text).copyOptionsFrom(this.widget));
        this.textBox.withSize(this.widget.getWidth(), this.widget.getHeight());
        this.textBox.withPosition(this.widget.getX(), this.widget.getY());

        ListWidget old = this.options;
        this.options = addRenderableWidget(new ListWidget(this.width() - 4, this.height() - 3));
        this.options.setPosition(this.x() + 2, this.y() + 2);
        this.options.update(old);

        setFocused(textBox);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (super.mouseClicked(event, bl)) {
            return true;
        }
        this.onClose();
        return true;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        if (this.filteredSuggestions.isEmpty()) return;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIST, this.x(), this.y(), this.width(), this.height());
    }

    public void filter(String text) {
        this.filteredSuggestions.clear();
        if (!text.isEmpty() || this.widget.alwaysShow) {
            this.widget.suggestions.apply(text)
                    .stream()
                    .map(this.widget.mapper)
                    .map(it -> new AutocompleteEntry(this.width() - 3, ENTRY_HEIGHT, it, () -> {
                        this.text.set(it);
                        this.textBox.keyPressed(new KeyEvent(InputConstants.KEY_RETURN, 0, 0));
                        this.onClose();
                    }))
                    .forEach(this.filteredSuggestions::add);
        }

        this.options.set(this.filteredSuggestions);
        this.options.setHeight(this.height() - 3);
        this.options.setPosition(this.x() + 2, this.y() + 2);
    }
}
