package earth.terrarium.heracles.client.components.widgets.textbox;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.components.widgets.WidgetSprites;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.Optionull;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextBox extends BaseWidget {

    private static final WidgetSprites SPRITES = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/textbox/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/textbox/hovered.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/textbox/focused.png")
    );
    protected static final int TEXT_COLOR = 0xe0e0e0;
    protected static final int ERROR_COLOR = 0xFF5555;
    protected static final int PLACEHOLDER_COLOR = 0xD0D0D0;
    protected static final int PADDING = 4;

    protected final Font font = Minecraft.getInstance().font;

    private final int maxLength;
    private final Predicate<String> filter;
    private final Consumer<String> responder;

    protected String placeholder = "";
    protected String value;
    private boolean shiftPressed;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;

    public TextBox(TextBox box, String value, int width, int height, int maxLength) {
        this(box, value, width, height, maxLength, s -> true, s -> {});
    }

    public TextBox(TextBox box, String value, int width, int height, int maxLength, Predicate<String> filter, Consumer<String> responder) {
        super(width, height);
        this.maxLength = maxLength;
        this.filter = filter;
        this.responder = responder;
        this.value = Optionull.mapOrDefault(box, TextBox::getValue, value);

        this.setCursorPosition(this.value.length());
        this.setHighlightPos(this.cursorPos);
    }

    public void setValue(String text) {
        if (this.filter.test(text)) {
            if (text.length() > this.maxLength) {
                this.value = text.substring(0, this.maxLength);
            } else {
                this.value = text;
            }

            this.moveCursorTo(this.value.length());
            this.setHighlightPos(this.cursorPos);
            this.onValueChange();
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int min = Math.min(this.cursorPos, this.highlightPos);
        int max = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(min, max);
    }

    public void insertText(String textToWrite) {
        int min = Math.min(this.cursorPos, this.highlightPos);
        int max = Math.max(this.cursorPos, this.highlightPos);
        int k = this.maxLength - this.value.length() - (min - max);
        String string = SharedConstants.filterText(textToWrite);
        int l = string.length();
        if (k < l) {
            string = string.substring(0, k);
            l = k;
        }

        String string2 = new StringBuilder(this.value).replace(min, max, string).toString();
        if (this.filter.test(string2)) {
            this.value = string2;
            this.setCursorPosition(min + l);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange();
        }
    }

    protected void onValueChange() {
        this.responder.accept(this.value);
    }

    private void deleteText(int count) {
        if (this.value.isEmpty()) return;

        if (Screen.hasControlDown()) {
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
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = this.getCursorPos(num);
                int j = Math.min(i, this.cursorPos);
                int k = Math.max(i, this.cursorPos);
                if (j != k) {
                    String string = new StringBuilder(this.value).delete(j, k).toString();
                    if (this.filter.test(string)) {
                        this.value = string;
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

        for (int k = 0; k < j; ++k) {
            if (!bl) {
                int l = this.value.length();
                i = this.value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (i < l && this.value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while (i > 0 && this.value.charAt(i - 1) == ' ') {
                    --i;
                }

                while (i > 0 && this.value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    private int getCursorPos(int delta) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, delta);
    }

    public void moveCursorTo(int pos) {
        this.setCursorPosition(pos);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange();
    }

    public void setCursorPosition(int pos) {
        this.cursorPos = Mth.clamp(pos, 0, this.value.length());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isVisible() || !this.isFocused()) return false;

        this.shiftPressed = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            this.moveCursorTo(this.value.length());
            this.setHighlightPos(0);
            return true;
        } else if (Screen.isCopy(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
        } else if (Screen.isPaste(keyCode)) {
            this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            return true;
        } else if (Screen.isCut(keyCode)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            this.insertText("");
            return true;
        } else {
            return switch (keyCode) {
                case InputConstants.KEY_BACKSPACE -> {
                    this.shiftPressed = false;
                    this.deleteText(-1);
                    this.shiftPressed = Screen.hasShiftDown();
                    yield true;
                }
                case InputConstants.KEY_DELETE -> {
                    this.shiftPressed = false;
                    this.deleteText(1);
                    this.shiftPressed = Screen.hasShiftDown();
                    yield true;
                }
                case InputConstants.KEY_RIGHT -> {
                    int pos = Screen.hasControlDown() ? this.getWordPosition(1) : this.getCursorPos(1);
                    this.moveCursorTo(pos);
                    yield true;
                }
                case InputConstants.KEY_LEFT -> {
                    int pos = Screen.hasControlDown() ? this.getWordPosition(-1) : this.getCursorPos(-1);
                    this.moveCursorTo(pos);
                    yield true;
                }
                case InputConstants.KEY_HOME -> {
                    this.moveCursorTo(0);
                    yield true;
                }
                case InputConstants.KEY_END -> {
                    this.moveCursorTo(this.value.length());
                    yield true;
                }
                default -> false;
            };
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.isVisible() || !this.isFocused()) return false;
        if (SharedConstants.isAllowedChatCharacter(codePoint)) {
            this.insertText(Character.toString(codePoint));
            return true;
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int relativeX = Mth.floor(mouseX) - this.getX() - PADDING;
        String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.width - PADDING * 2);
        this.moveCursorTo(this.font.plainSubstrByWidth(string, relativeX).length() + this.displayPos);
    }

    public int getTextColor() {
        return TEXT_COLOR;
    }

    public void setPlaceholder(Component placeholder) {
        this.placeholder = placeholder.getString();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isVisible()) {

            boolean showPlaceholder = this.value.isEmpty() && !this.placeholder.isEmpty();

            String value = showPlaceholder ? this.placeholder : this.value;

            ResourceLocation texture = SPRITES.get(this.isHoveredOrFocused(), !this.isActive());

            UIUtils.blitWithEdge(graphics, texture, this.getX(), this.getY(), this.width, this.height, 4);

            int displayCursorDiff = this.cursorPos - this.displayPos;
            int displayHighlightDiff = this.highlightPos - this.displayPos;
            String truncatedValue = this.font.plainSubstrByWidth(value.substring(this.displayPos), this.width - 8);
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
                n = graphics.drawString(this.font, string2, l, m, showPlaceholder ? PLACEHOLDER_COLOR : getTextColor(), false) + 1;
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
                graphics.drawString(this.font, truncatedValue.substring(displayCursorDiff), n, m, showPlaceholder ? PLACEHOLDER_COLOR : getTextColor(), false);
            }

            if (showCursor) {
                graphics.fill(RenderType.guiOverlay(), o - 1, m - 1, o, m + 1 + 9, -3092272);
            }

            if (displayHighlightDiff != displayCursorDiff) {
                int p = l + this.font.width(truncatedValue.substring(0, displayHighlightDiff));
                this.renderHighlight(graphics, o, m - 1, p - 1, m + 1 + 9);
            }
        }
    }

    private void renderHighlight(GuiGraphics graphics, int minX, int minY, int maxX, int maxY) {
        int x1 = Mth.clamp(Math.min(minX, maxX), this.getX(), this.getX() + this.width - PADDING);
        int x2 = Mth.clamp(Math.max(minX, maxX), this.getX(), this.getX() + this.width - PADDING);
        int y1 = Math.min(minY, maxY);
        int y2 = Math.max(minY, maxY);
        graphics.fill(RenderType.guiTextHighlight(), x1, y1, x2, y2, 0xff0000ff);
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
        int length = this.value.length();
        this.highlightPos = Mth.clamp(position, 0, length);
        if (this.displayPos > length) {
            this.displayPos = length;
        }

        int textWidth = this.width - PADDING * 2;
        String string = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), textWidth);
        int k = string.length() + this.displayPos;
        if (this.highlightPos == this.displayPos) {
            this.displayPos -= this.font.plainSubstrByWidth(this.value, textWidth, true).length();
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
