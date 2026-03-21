package earth.terrarium.olympus.client.components.textbox;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.textbox.utils.TextBoxStringUtils;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.utils.ListenableState;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class TextBox extends BaseWidget {
    protected static final int PADDING = 4;
    protected Color textColor = MinecraftColors.GRAY;
    protected Color errorColor = MinecraftColors.RED;
    protected Color placeholderColor = MinecraftColors.DARK_GRAY;
    protected WidgetSprites sprites = UIConstants.TEXTBOX;

    protected final Font font = Minecraft.getInstance().font;
    protected final State<String> state;

    private Predicate<String> filter = s -> true;
    private Consumer<String> onChange = s -> {};
    private Consumer<String> onEnter = s -> {};
    protected String placeholder = "";
    private boolean shiftPressed;
    private int maxLength = Short.MAX_VALUE;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;


    public TextBox(State<@NotNull String> state) {
        this.state = Util.make(ListenableState.of(state), listenable ->
                listenable.registerListener(newData -> this.onChange.accept(newData))
        );

        this.setCursorPosition(this.state.get().length());
        this.setHighlightPos(this.cursorPos);
        this.displayPos = 0;
    }

    @ApiStatus.Internal
    public TextBox copyOptionsFrom(TextBox other) {
        this.filter = other.filter;
        this.onEnter = other.onEnter;
        this.placeholder = other.placeholder;
        this.maxLength = other.maxLength;
        this.textColor = other.textColor;
        this.errorColor = other.errorColor;
        this.placeholderColor = other.placeholderColor;
        this.sprites = other.sprites;
        return this;
    }

    public TextBox withPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public TextBox withMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public TextBox withFilter(Predicate<String> filter) {
        this.filter = filter;
        return this;
    }

    public TextBox withEnterCallback(Consumer<String> onEnter) {
        this.onEnter = onEnter;
        return this;
    }

    public TextBox withChangeCallback(Consumer<String> onChange) {
        this.onChange = onChange;
        return this;
    }

    public TextBox withTextColor(Color color) {
        this.textColor = color;
        return this;
    }

    public TextBox withErrorColor(Color color) {
        this.errorColor = color;
        return this;
    }

    public TextBox withPlaceholderColor(Color color) {
        this.placeholderColor = color;
        return this;
    }

    public TextBox withTexture(WidgetSprites sprites) {
        this.sprites = sprites;
        return this;
    }

    public String getValue() {
        return this.state.get();
    }

    public void setValue(String text) {
        if (this.filter.test(text)) {
            if (text.length() > this.maxLength) {
                this.state.set(text.substring(0, this.maxLength));
            } else {
                this.state.set(text);
            }

            this.moveCursorTo(this.state.get().length());
            this.setHighlightPos(this.cursorPos);
        }
    }

    public String getHighlighted() {
        int min = Math.min(this.cursorPos, this.highlightPos);
        int max = Math.max(this.cursorPos, this.highlightPos);
        return this.state.get().substring(min, max);
    }

    public void insertText(String textToWrite) {
        int min = Math.min(this.cursorPos, this.highlightPos);
        int max = Math.max(this.cursorPos, this.highlightPos);
        int k = this.maxLength - this.state.get().length() - (min - max);
        String string = TextBoxStringUtils.filterText(textToWrite);
        int l = string.length();
        if (k < l) {
            string = string.substring(0, k);
            l = k;
        }

        String string2 = new StringBuilder(this.state.get()).replace(min, max, string).toString();
        if (this.filter.test(string2)) {
            this.state.set(string2);
            this.setCursorPosition(min + l);
            this.setHighlightPos(this.cursorPos);
        }
    }

    private void deleteText(KeyEvent event, int count) {
        if (this.state.get().isEmpty()) return;

        if (event.hasControlDown()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(count) - this.cursorPos);
            }
        } else {
            this.deleteChars(count);
        }
    }

    public void deleteChars(int num) {
        if (!this.state.get().isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = this.getCursorPos(num);
                int j = Math.min(i, this.cursorPos);
                int k = Math.max(i, this.cursorPos);
                if (j != k) {
                    String string = new StringBuilder(this.state.get()).delete(j, k).toString();
                    if (this.filter.test(string)) {
                        this.state.set(string);
                        this.moveCursorTo(j);
                    }
                }
            }
        }
    }

    public int getWordPosition(int numWords) {
        return this.getWordPosition(numWords, this.cursorPos);
    }

    private int getWordPosition(int n, int pos) {
        int i = pos;
        boolean bl = n < 0;
        int j = Math.abs(n);

        String value = this.state.get();
        for (int k = 0; k < j; ++k) {
            if (!bl) {
                int l = value.length();
                i = value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (i < l && value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while (i > 0 && value.charAt(i - 1) == ' ') {
                    --i;
                }

                while (i > 0 && value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    private int getCursorPos(int delta) {
        return Util.offsetByCodepoints(this.state.get(), this.cursorPos, delta);
    }

    public void moveCursorTo(int pos) {
        this.setCursorPosition(pos);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }
    }

    public void setCursorPosition(int pos) {
        this.cursorPos = Mth.clamp(pos, 0, this.state.get().length());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isVisible() || !this.isFocused()) return false;

        this.shiftPressed = event.hasShiftDown();
        if (event.isSelectAll()) {
            this.moveCursorTo(this.state.get().length());
            this.setHighlightPos(0);
            return true;
        } else if (event.isCopy()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
        } else if (event.isPaste()) {
            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        } else if (event.isCut()) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            this.insertText("");
            return true;
        } else {
            return switch (event.input()) {
                case InputConstants.KEY_BACKSPACE -> {
                    this.shiftPressed = false;
                    this.deleteText(event, -1);
                    this.shiftPressed = event.hasShiftDown();
                    yield true;
                }
                case InputConstants.KEY_DELETE -> {
                    this.shiftPressed = false;
                    this.deleteText(event, 1);
                    this.shiftPressed = event.hasShiftDown();
                    yield true;
                }
                case InputConstants.KEY_RIGHT -> {
                    int pos = event.hasControlDown() ? this.getWordPosition(1) : this.getCursorPos(1);
                    this.moveCursorTo(pos);
                    yield true;
                }
                case InputConstants.KEY_LEFT -> {
                    int pos = event.hasControlDown() ? this.getWordPosition(-1) : this.getCursorPos(-1);
                    this.moveCursorTo(pos);
                    yield true;
                }
                case InputConstants.KEY_HOME -> {
                    this.moveCursorTo(0);
                    yield true;
                }
                case InputConstants.KEY_END -> {
                    this.moveCursorTo(this.state.get().length());
                    yield true;
                }
                case InputConstants.KEY_RETURN -> {
                    this.onEnter.accept(this.state.get());
                    yield true;
                }
                default -> false;
            };
        }
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.isVisible() || !this.isFocused()) return false;
        if (!event.isAllowedChatCharacter()) return false;
        this.insertText(Character.toString(event.codepoint()));
        return true;
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean bl) {
        int relativeX = Mth.floor(event.x()) - this.getX() - PADDING;
        String string = TextBoxStringUtils.plainHeadByWidth(this.font, this.state.get().substring(this.displayPos), this.width - PADDING * 2);
        this.moveCursorTo(TextBoxStringUtils.plainHeadByWidth(this.font, string, relativeX).length() + this.displayPos);
    }

    public int getTextColor() {
        if (this.state.get().isEmpty()) {
            return ARGB.color(0xFF, placeholderColor.getValue());
        } else {
            return ARGB.color(0xFF, this.filter.test(this.state.get()) ? textColor.getValue() : errorColor.getValue());
        }
    }

    public void setPlaceholder(Component placeholder) {
        this.placeholder = placeholder.getString();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isVisible()) {
            String value = this.state.get().isEmpty() && !this.placeholder.isEmpty() ? this.placeholder : this.state.get();

            Identifier texture = sprites.get(this.active, this.isHoveredOrFocused());

            graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, texture, this.getX(), this.getY(), this.width, this.height);

            int displayCursorDiff = this.cursorPos - this.displayPos;
            int displayHighlightDiff = this.highlightPos - this.displayPos;
            String truncatedValue = TextBoxStringUtils.plainHeadByWidth(this.font, value.substring(this.displayPos), this.width - 8);
            boolean cursorVisible = displayCursorDiff >= 0 && displayCursorDiff <= truncatedValue.length();
            boolean showCursor = this.isFocused() && cursorVisible && System.currentTimeMillis() / 500 % 2 == 0;
            int l = this.getX() + 4;
            int m = this.getY() + (this.height - 8) / 2;
            int n = l;
            if (displayHighlightDiff > truncatedValue.length()) {
                displayHighlightDiff = truncatedValue.length();
            }

            if (!truncatedValue.isEmpty()) {
                String string2 = cursorVisible ? truncatedValue.substring(0, displayCursorDiff) : truncatedValue;
                graphics.drawString(this.font, TextBoxStringUtils.format(string2), l, m, getTextColor(), false);
                n = l + this.font.width(string2) + 1;
            }

            boolean bl3 = this.cursorPos < value.length() || value.length() >= this.maxLength;
            int o = n;
            if (!cursorVisible) {
                o = displayCursorDiff > 0 ? l + this.width : l;
            } else if (bl3) {
                o = n - 1;
                --n;
            }

            if (!truncatedValue.isEmpty() && cursorVisible && displayCursorDiff < truncatedValue.length()) {
                graphics.drawString(this.font, TextBoxStringUtils.format(truncatedValue.substring(displayCursorDiff)), n, m, getTextColor(), false);
            }

            if (showCursor) {
                graphics.fill(RenderPipelines.GUI, o - 1, m - 1, o, m + 1 + 9, 0xffd0d0d0);
            }

            if (displayHighlightDiff != displayCursorDiff) {
                int p = l + TextBoxStringUtils.width(this.font, truncatedValue.substring(0, displayHighlightDiff));
                this.renderHighlight(graphics, o, m - 1, p - 1, m + 1 + 9);
            }
        }
    }

    private void renderHighlight(GuiGraphics graphics, int minX, int minY, int maxX, int maxY) {
        int x1 = Mth.clamp(Math.min(minX, maxX), this.getX(), this.getX() + this.width - PADDING);
        int x2 = Mth.clamp(Math.max(minX, maxX), this.getX(), this.getX() + this.width - PADDING);
        int y1 = Math.min(minY, maxY);
        int y2 = Math.max(minY, maxY);
        graphics.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x1, y1, x2, y2, 0xff0000ff);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return this.visible ? super.nextFocusPath(event) : null;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible
            && mouseX >= (double) this.getX()
            && mouseX < (double) (this.getX() + this.width)
            && mouseY >= (double) this.getY()
            && mouseY < (double) (this.getY() + this.height);
    }

    public void setHighlightPos(int position) {
        int length = this.state.get().length();
        this.highlightPos = Mth.clamp(position, 0, length);
        if (this.displayPos > length) {
            this.displayPos = length;
        }

        int textWidth = this.width - PADDING * 2;
        String string = TextBoxStringUtils.plainHeadByWidth(this.font, this.state.get().substring(this.displayPos), textWidth);
        int k = string.length() + this.displayPos;
        if (this.highlightPos == this.displayPos) {
            this.displayPos -= TextBoxStringUtils.plainTailByWidth(this.font, this.state.get(), textWidth).length();
        }

        if (this.highlightPos > k) {
            this.displayPos += this.highlightPos - k;
        } else if (this.highlightPos <= this.displayPos) {
            this.displayPos -= this.displayPos - this.highlightPos;
        }

        this.displayPos = Mth.clamp(this.displayPos, 0, length);
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.TEXT;
    }
}
