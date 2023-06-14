package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Dropdown<T> extends AbstractWidget implements Renderable {

    private static final int MAX_OPTIONS_SHOWN = 10;

    private final List<T> options = new ArrayList<>();
    private final Function<T, Component> mapper;
    private final Component placeholder;

    private double scrollAmount = 0;
    private T selectedOption = null;
    private Consumer<T> onSelect;

    private boolean lostFocus = false;

    public Dropdown(int x, int y, int width, int height, Component placeholder, Function<T, Component> mapper) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.placeholder = placeholder;
        this.mapper = mapper;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        if (lostFocus) { // Is here because after the click logic of a widget is called, the focus is set if the widget is clicked
            lostFocus = false;
            setFocused(false);
        }
        this.isHovered = isMouseOver(i, j);
        int x = this.getX();
        int y = this.getY();
        int width = this.getWidth();
        int height = this.getHeight();
        Font font = Minecraft.getInstance().font;
        graphics.drawString(
            font,
            ellipsize(Optionull.mapOrDefault(selectedOption, mapper, this.placeholder), width - 20, font), x + 3, y + 3, selectedOption == null ? 0x808080 : 0xFFFFFF,
            false
        );
        graphics.drawString(
            font,
            isFocused() ? "▲" : "▼", x + width - 10, y + 3, 0xFFFFFF,
            false
        );

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        if (isFocused()) {
            graphics.blitNineSliced(BaseModal.TEXTURE, x - 1, y + height + 1, width + 2, (10 * Math.min(MAX_OPTIONS_SHOWN, options.size())) + 2, 2, 2, 2, 2, 128, 128, 128, 1);

            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(0, 0, 10); // This is because minecraft has a weird bug with shadowed text rendered behind other text
                try (var ignored = RenderUtils.createScissor(Minecraft.getInstance(), graphics, x, y + height, width, 10 * Math.min(MAX_OPTIONS_SHOWN, options.size()))) {
                    int i1 = 0;
                    for (T option : options) {
                        int y1 = y + height + 1 + (i1 * 10) - (int) scrollAmount;
                        boolean isHovered = i >= x && j >= y1 && i < x + width && j < y1 + 10;
                        if (isHovered) {
                            graphics.fill(x, y1, x + width, y1 + 10, 0xFF808080);
                        }
                        graphics.drawString(
                            font,
                            ellipsize(Optionull.mapOrDefault(option, mapper, this.placeholder), width, font), x + 3, y1 + 1, 0xFFFFFF,
                            false
                        );
                        i1++;
                    }
                }
            }
        }
    }

    private static FormattedCharSequence ellipsize(Component text, int width, Font font) {
        if (font.width(text) <= width) {
            return text.getVisualOrderText();
        }
        var formattedText = font.split(text, width - font.width("..."));
        return FormattedCharSequence.composite(formattedText.get(0), FormattedCharSequence.forward("...", Style.EMPTY));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isFocused()) return false;
        if (options.size() > MAX_OPTIONS_SHOWN) {
            move(delta * 10);
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isFocused() && isMouseOver(mouseX, mouseY)) {
            lostFocus = true;
        }
        boolean mouseOver = isMouseOver(mouseX, mouseY);
        setFocused(mouseOver);
        if (isFocused() && button == 0 && isMouseOver(mouseX, mouseY) && mouseY > (this.getY() + this.getHeight() + 1)) {
            int i = 0;
            for (T option : options) {
                int y = this.getY() + this.getHeight() + 1 + (i * 10) - (int) scrollAmount;
                if (mouseY >= y && mouseY < y + 10) {
                    selectedOption = option;
                    if (onSelect != null) {
                        onSelect.accept(option);
                    }
                    lostFocus = true;
                    return true;
                }
                i++;
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
        return isFocused()
            && mouseX >= (double) this.getX()
            && mouseX < (double) (this.getX() + this.width)
            && mouseY >= (double) this.getY() + this.height + 1
            && mouseY < (double) (this.getY() + this.height + 1 + (10 * Math.min(MAX_OPTIONS_SHOWN, options.size())));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (options.size() > MAX_OPTIONS_SHOWN && isFocused()) {
            if (keyCode == InputConstants.KEY_DOWN) {
                move(-10);
                return true;
            } else if (keyCode == InputConstants.KEY_UP) {
                move(10);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void move(double amount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount + amount, 0.0D, Math.max(0, (options.size() * 10) - (MAX_OPTIONS_SHOWN * 10)));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Nullable
    public T value() {
        return selectedOption;
    }

    public void setOptions(Collection<T> options) {
        this.scrollAmount = 0;
        this.selectedOption = null;
        this.options.clear();
        this.options.addAll(options);
    }

    public void setResponder(Consumer<T> onSelect) {
        this.onSelect = onSelect;
    }

    public void setSelectedOption(T option) {
        this.selectedOption = option;
    }
}
