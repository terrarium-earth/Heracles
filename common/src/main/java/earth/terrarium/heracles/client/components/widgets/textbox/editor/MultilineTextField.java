package earth.terrarium.heracles.client.components.widgets.textbox.editor;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import java.util.List;

public class MultilineTextField {

	private final Font font;
	private final List<MultilineTextField.StringView> lines = Lists.newArrayList();
	private final EditorHistory history = new EditorHistory();


	private String value;
	private int cursor;
	private int selectCursor;
	private boolean selecting;
	private final int width;
	private Runnable cursorListener = () -> {};

	private String oldValue = null;

	public MultilineTextField(Font font, int i) {
		this.font = font;
		this.width = i;
		this.value = "";
		this.cursor = 0;
		this.selectCursor = 0;
		this.reflowDisplayLines();
		this.cursorListener.run();
	}

	public void setCursorListener(Runnable cursorListener) {
		this.cursorListener = cursorListener;
	}

	public void copy(MultilineTextField field) {
		this.setValue(field.value, true);
		this.cursor = field.cursor;
		this.selectCursor = field.selectCursor;
		this.selecting = field.selecting;
		this.reflowDisplayLines();
		this.cursorListener.run();
	}

	public void setValue(String fullText, boolean updateHistory) {
		this.value = fullText;
		this.cursor = this.value.length();
		this.selectCursor = this.cursor;
		this.onValueChange(updateHistory);
	}

	public String value() {
		return this.value;
	}

	public void insertText(String text) {
		if (!text.isEmpty() || this.hasSelection()) {
			text = text.replace("§", "&&");
			String string = SharedConstants.filterText(text, true);
			MultilineTextField.StringView stringView = this.getSelected();
			this.value = new StringBuilder(this.value).replace(stringView.beginIndex, stringView.endIndex, string).toString();
			this.cursor = stringView.beginIndex + string.length();
			this.selectCursor = this.cursor;
			this.onValueChange(true);
		}
	}

	public void deleteText(int length) {
		if (!this.hasSelection()) {
			this.selectCursor = Mth.clamp(this.cursor + length, 0, this.value.length());
		}

		this.insertText("");
	}

	public int cursor() {
		return this.cursor;
	}

	public int selectCursor() {
		return this.selectCursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	public void setSelectCursor(int selectCursor) {
		this.selectCursor = selectCursor;
	}

	public void setSelecting(boolean selecting) {
		this.selecting = selecting;
	}

	public MultilineTextField.StringView getSelected() {
		return new MultilineTextField.StringView(Math.min(this.selectCursor, this.cursor), Math.max(this.selectCursor, this.cursor));
	}

	public int getLineAtCursor() {
		for(int i = 0; i < this.lines.size(); ++i) {
			MultilineTextField.StringView view = this.lines.get(i);
			if (this.cursor >= view.beginIndex && this.cursor <= view.endIndex) {
				return i;
			}
		}

		return -1;
	}

	public MultilineTextField.StringView getLineView(int i) {
		return this.lines.get(Mth.clamp(i, 0, this.lines.size() - 1));
	}

	public void seekCursor(Whence whence, int i) {
		switch (whence) {
			case ABSOLUTE -> this.cursor = i;
			case RELATIVE -> this.cursor += i;
			case END -> this.cursor = this.value.length() + i;
		}

		this.cursor = Mth.clamp(this.cursor, 0, this.value.length());
		this.cursorListener.run();
		if (!this.selecting) {
			this.selectCursor = this.cursor;
		}
	}

	public void seekCursorLine(int i) {
		if (i != 0) {
			int j = this.font.width(this.value.substring(this.getCursorLineView().beginIndex, this.cursor)) + 2;
			MultilineTextField.StringView stringView = this.getCursorLineView(i);
			int k = this.font.plainSubstrByWidth(this.value.substring(stringView.beginIndex, stringView.endIndex), j).length();
			this.seekCursor(Whence.ABSOLUTE, stringView.beginIndex + k);
		}
	}

	public void seekCursorToPoint(double d, double e) {
		int i = Mth.floor(d);
		int j = Mth.floor(e / 9.0);
		MultilineTextField.StringView stringView = this.lines.get(Mth.clamp(j, 0, this.lines.size() - 1));
		int k = this.font.plainSubstrByWidth(this.value.substring(stringView.beginIndex, stringView.endIndex), i).length();
		this.seekCursor(Whence.ABSOLUTE, stringView.beginIndex + k);
	}

	public boolean keyPressed(int i) {
		this.selecting = Screen.hasShiftDown();
		if (Screen.isSelectAll(i)) {
			this.cursor = this.value.length();
			this.selectCursor = 0;
			return true;
		} else if (Screen.isCopy(i)) {
			Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
			return true;
		} else if (Screen.isPaste(i)) {
			this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
			return true;
		} else if (Screen.isCut(i)) {
			Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
			this.insertText("");
			return true;
		} else if (isUndo(i)) {
			this.setValue(this.history.undo(this.value), false);
			return true;
		} else if (isRedo(i)) {
			this.setValue(this.history.redo(this.value), false);
			return true;
		} else {
			return switch (i) {
				case InputConstants.KEY_TAB -> {
					this.insertText("    ");
					yield true;
				}
				case InputConstants.KEY_RETURN, InputConstants.KEY_NUMPADENTER -> {
					this.insertText("\n");
					yield true;
				}
				case InputConstants.KEY_BACKSPACE -> {
					if (Screen.hasControlDown()) {
						StringView stringView = this.getPreviousWord();
						this.deleteText(stringView.beginIndex - this.cursor);
					} else {
						this.deleteText(-1);
					}
					yield true;
				}
				case InputConstants.KEY_DELETE -> {
					if (Screen.hasControlDown()) {
						StringView stringView = this.getNextWord();
						this.deleteText(stringView.beginIndex - this.cursor);
					} else {
						this.deleteText(1);
					}
					yield true;
				}
				case InputConstants.KEY_RIGHT -> {
					if (Screen.hasControlDown()) {
						StringView stringView = this.getNextWord();
						this.seekCursor(Whence.ABSOLUTE, stringView.beginIndex);
					} else {
						this.seekCursor(Whence.RELATIVE, 1);
					}
					yield true;
				}
				case InputConstants.KEY_LEFT -> {
					if (Screen.hasControlDown()) {
						StringView stringView = this.getPreviousWord();
						this.seekCursor(Whence.ABSOLUTE, stringView.beginIndex);
					} else {
						this.seekCursor(Whence.RELATIVE, -1);
					}
					yield true;
				}
				case InputConstants.KEY_DOWN -> {
					if (!Screen.hasControlDown()) {
						this.seekCursorLine(1);
					}
					yield true;
				}
				case InputConstants.KEY_UP -> {
					if (!Screen.hasControlDown()) {
						this.seekCursorLine(-1);
					}
					yield true;
				}
				case InputConstants.KEY_PAGEUP -> {
					this.seekCursor(Whence.ABSOLUTE, 0);
					yield true;
				}
				case InputConstants.KEY_PAGEDOWN -> {
					this.seekCursor(Whence.END, 0);
					yield true;
				}
				case InputConstants.KEY_HOME -> {
					if (Screen.hasControlDown()) {
						this.seekCursor(Whence.ABSOLUTE, 0);
					} else {
						this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().beginIndex);
					}
					yield true;
				}
				case InputConstants.KEY_END -> {
					if (Screen.hasControlDown()) {
						this.seekCursor(Whence.END, 0);
					} else {
						this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().endIndex);
					}
					yield true;
				}
				default -> false;
			};
		}
	}

	public List<MultilineTextField.StringView> lines() {
		return this.lines;
	}

	public boolean hasSelection() {
		return this.selectCursor != this.cursor;
	}

	@VisibleForTesting
	public String getSelectedText() {
		MultilineTextField.StringView stringView = this.getSelected();
		return this.value.substring(stringView.beginIndex, stringView.endIndex);
	}

	private MultilineTextField.StringView getCursorLineView() {
		return this.getCursorLineView(0);
	}

	private MultilineTextField.StringView getCursorLineView(int i) {
		int j = this.getLineAtCursor();
		if (j < 0) {
			throw new IllegalStateException("Cursor is not within text (cursor = " + this.cursor + ", length = " + this.value.length() + ")");
		} else {
			return this.lines.get(Mth.clamp(j + i, 0, this.lines.size() - 1));
		}
	}

	public StringView getPreviousWord() {
		if (this.value.isEmpty()) {
			return StringView.EMPTY;
		} else {
			int i = Mth.clamp(this.cursor, 0, this.value.length() - 1);

			while(i > 0 && Character.isWhitespace(this.value.charAt(i - 1))) {
				--i;
			}

			while(i > 0 && !Character.isWhitespace(this.value.charAt(i - 1))) {
				--i;
			}

			return new StringView(i, this.getWordEndPosition(i));
		}
	}

	public StringView getNextWord() {
		if (this.value.isEmpty()) {
			return StringView.EMPTY;
		} else {
			int i = Mth.clamp(this.cursor, 0, this.value.length() - 1);

			while(i < this.value.length() && !Character.isWhitespace(this.value.charAt(i))) {
				++i;
			}

			while(i < this.value.length() && Character.isWhitespace(this.value.charAt(i))) {
				++i;
			}

			return new StringView(i, this.getWordEndPosition(i));
		}
	}

	private int getWordEndPosition(int cursor) {
		int i = cursor;

		while(i < this.value.length() && !Character.isWhitespace(this.value.charAt(i))) {
			++i;
		}

		return i;
	}

	private void onValueChange(boolean updateHistory) {
		 if (updateHistory) {
			 if (this.oldValue != null) {
				 this.history.push(this.oldValue);
			 }
			 this.oldValue = this.value;
		 }
		this.reflowDisplayLines();
		this.cursorListener.run();
	}

	private void reflowDisplayLines() {
		this.lines.clear();
		if (this.value.isEmpty()) {
			this.lines.add(MultilineTextField.StringView.EMPTY);
		} else {
			this.font
				.getSplitter()
				.splitLines(this.value, this.width, Style.EMPTY, false, (style, i, j) -> this.lines.add(new MultilineTextField.StringView(i, j)));
			if (this.value.charAt(this.value.length() - 1) == '\n') {
				this.lines.add(new MultilineTextField.StringView(this.value.length(), this.value.length()));
			}
		}
	}

	public record StringView(int beginIndex, int endIndex) {
		public static final MultilineTextField.StringView EMPTY = new MultilineTextField.StringView(0, 0);
	}

	public static boolean isUndo(int keyCode) {
		return keyCode == InputConstants.KEY_Z && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
	}

	public static boolean isRedo(int keyCode) {
		return keyCode == InputConstants.KEY_Y && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
	}
}
