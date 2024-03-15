package earth.terrarium.heracles.client.components.widgets.textbox.editor;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class MultiLineTextBox extends AbstractScrollWidget implements CursorWidget {

	protected final Font font;
	protected final MultilineTextField field;
	protected final TextHighlighter highlighter;

	public MultiLineTextBox(MultiLineTextBox box, Font font, int width, int height, TextHighlighter highlighter) {
		this(font, 0, 0, width, height, highlighter);
		if (box != null) this.copy(box);
	}

	public MultiLineTextBox(Font font, int x, int y, int width, int height, TextHighlighter highlighter) {
		super(x, y, width, height, CommonComponents.EMPTY);
		this.font = font;
		this.field = new MultilineTextField(font, width - this.totalInnerPadding());
		this.field.setCursorListener(this::scrollToCursor);
		this.highlighter = highlighter;
	}

	public void setValue(String fullText) {
		this.field.setValue(fullText, true);
	}

	public void copy(MultiLineTextBox box) {
		this.field.copy(box.field);
	}

	public String getValue() {
		return this.field.value();
	}

	@Override
	public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.withinContentAreaPoint(mouseX, mouseY) && button == InputConstants.MOUSE_BUTTON_LEFT) {
			this.field.setSelecting(Screen.hasShiftDown());
			this.seekCursorScreen(mouseX, mouseY);
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
			return true;
		} else if (this.withinContentAreaPoint(mouseX, mouseY) && button == 0) {
			this.field.setSelecting(true);
			this.seekCursorScreen(mouseX, mouseY);
			this.field.setSelecting(Screen.hasShiftDown());
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.field.keyPressed(keyCode);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		String text = codePoint == '§' ? "&&" : Character.toString(codePoint);
		if (this.visible && this.isFocused() && codePoint >= ' ' && codePoint != 127) {
			this.field.insertText(text);
			return true;
		}
		return false;
	}

	@Override
	protected void renderBackground(GuiGraphics graphics) {

	}

	@Override
	protected void renderDecorations(GuiGraphics graphics) {
		if (this.scrollbarVisible()) {
			int scrollBarHeight = Mth.clamp((int)((float)(this.height * this.height) / (float)this.getInnerHeight() + 4f), 32, this.height);
			int scrollBarY = Math.max(this.getY(), (int)this.scrollAmount() * (this.height - scrollBarHeight) / this.getMaxScrollAmount() + this.getY());
			graphics.fill(
				this.getX() + this.width + 5, scrollBarY,
				this.getX() + this.width + 7, scrollBarY + scrollBarHeight,
				0xFFC0C0C0
			);
		}
	}

	@Override
	protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		String value = this.field.value();
		int lineX = this.getX() + this.innerPadding();
		int lineY = this.getY() + this.innerPadding();
		if (value.isEmpty() && this.isFocused()) {
			boolean showCursor = System.currentTimeMillis() / 500 % 2 == 0;
			if (showCursor) {
				graphics.fill(lineX, lineY, lineX + 1, lineY + 9, this.highlighter.getCursorColor());
			}
		} else {
			int cursor = this.field.cursor();
			boolean showCursor = this.isFocused() && System.currentTimeMillis() / 500 % 2 == 0;
			boolean isEndOfLine = cursor >= value.length();

			for(MultilineTextField.StringView view : this.field.lines()) {
				boolean shouldRender = this.withinContentAreaTopBottom(lineY, lineY + 9);
				if (shouldRender) {
					Component line = this.highlighter.highlight(value.substring(view.beginIndex(), view.endIndex()));
					if (showCursor && cursor >= view.beginIndex() && cursor <= view.endIndex()) {
						int width = this.font.width(this.getValue().substring(view.beginIndex(), cursor));
						int cursorX = isEndOfLine ? lineX + width + 1 : lineX + width;
						graphics.fill(cursorX - 1, lineY - 1, cursorX, lineY - 1 + 9, this.highlighter.getCursorColor());
					}
					graphics.drawString(this.font, line, lineX, lineY, this.highlighter.getTextColor(), false);
				}

				lineY += 9;
			}

			if (this.field.hasSelection()) {
				MultilineTextField.StringView selectedView = this.field.getSelected();
				int selectionX = this.getX() + this.innerPadding();
				lineY = this.getY() + this.innerPadding();

				for(MultilineTextField.StringView view : this.field.lines()) {
					if (selectedView.beginIndex() <= view.endIndex()) {
						if (view.beginIndex() > selectedView.endIndex()) {
							break;
						}

						if (this.withinContentAreaTopBottom(lineY, lineY + 9)) {
							int n = this.font.width(value.substring(view.beginIndex(), Math.max(selectedView.beginIndex(), view.beginIndex())));
							int o;
							if (selectedView.endIndex() > view.endIndex()) {
								o = this.width - this.innerPadding();
							} else {
								o = this.font.width(value.substring(view.beginIndex(), selectedView.endIndex()));
							}

							this.renderHighlight(graphics, selectionX + n, lineY, selectionX + o, lineY + 9);
						}

					}
					lineY += 9;
				}
			}
		}

		CursorUtils.setCursor(isHovered(), CursorScreen.Cursor.TEXT);
	}

	@Override
	public int getInnerHeight() {
		return 9 * this.field.lines().size();
	}

	@Override
	protected boolean scrollbarVisible() {
		return (double)this.field.lines().size() > this.getDisplayableLineCount();
	}

	@Override
	protected double scrollRate() {
		return 6.0;
	}

	private void renderHighlight(GuiGraphics graphics, int minX, int minY, int maxX, int maxY) {
		graphics.fill(RenderType.guiTextHighlight(), minX, minY, maxX, maxY, 0xff0000ff);
	}

	private void scrollToCursor() {
		double d = this.scrollAmount();
		MultilineTextField.StringView stringView = this.field.getLineView((int)(d / 9.0));
		if (this.field.cursor() <= stringView.beginIndex()) {
			d = this.field.getLineAtCursor() * 9;
		} else {
			MultilineTextField.StringView view = this.field.getLineView((int)((d + (double)this.height) / 9.0) - 1);
			if (this.field.cursor() > view.endIndex()) {
				d = this.field.getLineAtCursor() * 9 - this.height + 9 + this.totalInnerPadding();
			}
		}

		this.setScrollAmount(d);
	}

	private double getDisplayableLineCount() {
		return (double)(this.height - this.totalInnerPadding()) / 9.0;
	}

	private void seekCursorScreen(double mouseX, double mouseY) {
		double d = mouseX - (double)this.getX() - (double)this.innerPadding();
		double e = mouseY - (double)this.getY() - (double)this.innerPadding() + this.scrollAmount();
		this.field.seekCursorToPoint(d, e);
	}

	public MultilineTextField field() {
		return this.field;
	}

	@Override
	public CursorScreen.Cursor getCursor() {
		return CursorScreen.Cursor.TEXT;
	}
}
