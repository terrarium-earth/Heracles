package earth.terrarium.olympus.client.components.textbox.multiline;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.textbox.utils.TextBoxStringUtils;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.utils.ListenableState;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;

public class MultilineTextBox extends BaseWidget {

    private final MultilineTextInput state;

    private final Font font = Minecraft.getInstance().font;
    protected WidgetSprites sprites = UIConstants.TEXTBOX;

    private double scroll = - 1;
    private int lastHeight;
    private boolean scrollbarHovered = false;

    public MultilineTextBox(State<String> state) {
        this.state = new MultilineTextInput(state instanceof ListenableState<String> it ? it : new ListenableState<>(state));
    }

    public MultilineTextBox withTexture(WidgetSprites sprites) {
        this.sprites = sprites;
        return this;
    }

    protected void renderText(GuiGraphics graphics, int x, int y, int width) {
        var cursor = this.state.cursor();
        var selection = this.state.selection();
        var lines = this.state.lines(this.width - 8);

        for (var line : lines) {
            var text = this.state.value().substring(line.start(), line.end());

            graphics.drawString(this.font, TextBoxStringUtils.format(text), x, y, -1);

            if (this.state.hasSelection()) {
                if (line.contains(selection.end()) || line.contains(selection.start())) {
                    var startIndex = Math.max(selection.start() - line.start(), 0);
                    var endIndex = selection.end() - line.start();
                    var startX = TextBoxStringUtils.width(this.font, text.substring(0, startIndex)) + x;
                    var endX = endIndex > text.length() ? x + width : startX + TextBoxStringUtils.width(this.font, text.substring(startIndex, endIndex));
                    graphics.fill(startX, y, endX, y + this.font.lineHeight, 0x80AAAAAA);
                } else if (selection.contains(line.start()) && selection.contains(line.end())) {
                    graphics.fill(x, y, x + width, y + this.font.lineHeight, 0x80AAAAAA);
                }
            }

            if (line.contains(cursor)) {
                var first = text.substring(0, cursor - line.start());
                var cursorX = TextBoxStringUtils.width(this.font, first) + x;
                graphics.fill(cursorX, y, cursorX + 1, y + this.font.lineHeight, System.currentTimeMillis() % 1000 < 500 ? 0xFFFFFFFF : 0x00000000);
            }

            y += this.font.lineHeight;
        }

        this.lastHeight = lines.size() * this.font.lineHeight;

        if (this.scroll == -1) {
            this.setScroll(this.lastHeight - (this.height - 8));
        }
    }

    protected void setScroll(double scroll) {
        this.scroll = Mth.clamp(scroll, 0, Math.max(0, this.lastHeight - (this.height - 8)));
    }

    protected void changeScroll(double delta) {
        this.setScroll(this.scroll + delta);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.isVisible()) return;

        var texture = this.sprites.get(this.active, this.isHoveredOrFocused());

        graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, texture, getX(), getY(), getWidth(), getHeight());

        boolean renderScrollbar = this.lastHeight > this.height - 8;

        graphics.enableScissor(getX() + 2, getY() + 3, getX() + getWidth() - 4, getY() + getHeight() - 2);
        this.renderText(graphics, getX() + 6 - (renderScrollbar ? 2 : 0), (int) (getY() + 4 - scroll), getWidth() - 12);
        graphics.disableScissor();

        this.scrollbarHovered = false;
        if (renderScrollbar) {
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    UIConstants.id("lists/scroll/bar"),
                    getX() + getWidth() - 5,
                    getY() + 4,
                    2,
                    getHeight() - 8
            );

            var scrollBarHeight = (int) ((this.height - 8) / (double) this.lastHeight * (this.height - 8));
            var scrollBarY = (int) ((this.scroll / (double) this.lastHeight) * (this.height - 8));
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    UIConstants.id("lists/scroll/thumb"),
                    getX() + getWidth() - 6,
                    getY() + 4 + scrollBarY,
                    4,
                    scrollBarHeight
            );

            this.scrollbarHovered = mouseX > getX() + getWidth() - 7 && mouseY > getY() + 4 && mouseY < getY() + this.height - 4;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.isVisible()) return false;

        this.changeScroll(scrollY * -10);
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!this.isVisible()) return false;

        if (this.lastHeight > this.height - 8 && event.x() > getX() + getWidth() - 7) {
            this.setScroll((event.y() - getY() - 4) / (this.height - 8) * this.lastHeight);
            return true;
        }

        return this.state.onMouseClick(event.x() - getX() - 4, event.y() - getY() - 4 + scroll, event.input());
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!this.isVisible()) return false;

        if (this.lastHeight > this.height - 8 && event.x() > getX() + getWidth() - 7) {
            return this.mouseClicked(event, false);
        }

        return this.state.onMouseDrag(event.x() - getX() - 4, event.y() - getY() - 4 + scroll, event.input());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!this.isVisible()) return false;

        var result = this.state.onKeyPress(event);
        this.lastHeight = this.state.lines(this.width - 8).size() * this.font.lineHeight;
        this.setScroll(this.state.getLineAtCursor() * this.font.lineHeight);
        return result;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!this.isVisible()) return false;

        this.setScroll(this.state.getLineAtCursor() * this.font.lineHeight);
        return this.state.onCharTyped(event);
    }

    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return this.scrollbarHovered ? CursorScreen.Cursor.POINTER : CursorScreen.Cursor.TEXT;
    }
}
