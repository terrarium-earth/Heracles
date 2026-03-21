package earth.terrarium.olympus.client.components.textbox.multiline;

import earth.terrarium.olympus.client.components.textbox.utils.TextBoxStringUtils;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class MultilineTextState {

	protected final Font font = Minecraft.getInstance().font;
	protected final List<MultilineStringView> lines = new ArrayList<>();
	protected final ListenableState<String> state;

	protected String value;
	protected int cursor;
	protected int selectCursor;
	protected boolean selecting;

	protected int lastWidth;

	public MultilineTextState(ListenableState<String> state) {
		this.state = state;
		this.setValue(this.state.get());
		this.state.registerListener(this::setValue);
	}

	public void setValue(@NotNull String string) {
		if (string.equals(this.value)) return;

		this.value = string;
		this.cursor = this.value.length();
		this.selectCursor = this.cursor;
		this.reflowDisplayLines();

		if (this.state.get().equals(this.value)) return;
		this.state.set(string);
	}

	public String value() {
		return this.value;
	}

	protected void insertText(String string) {
		if (!string.isEmpty() || this.hasSelection()) {
			MultilineStringView selection = this.selection();
			this.value = new StringBuilder(this.value).replace(selection.start(), selection.end(), string).toString();
			this.cursor = selection.start() + string.length();
			this.selectCursor = this.cursor;
			this.reflowDisplayLines();
			this.state.set(this.value);
		}
	}

	protected void deleteText(int i) {
		if (!this.hasSelection()) {
			this.selectCursor = Mth.clamp(this.cursor + i, 0, this.value.length());
		}

		this.insertText("");
	}

	public int cursor() {
		return this.cursor;
	}

	protected MultilineStringView selection() {
		return new MultilineStringView(
				-1,
				Math.min(this.selectCursor, this.cursor),
				Math.max(this.selectCursor, this.cursor)
		);
	}

	public int getLineAtCursor() {
		for (int i = 0; i < this.lines.size(); i++) {
			MultilineStringView stringView = this.lines.get(i);
			if (this.cursor >= stringView.start() && this.cursor <= stringView.end()) {
				return i;
			}
		}

		return -1;
	}

	protected void seekCursor(Whence whence, int i) {
		switch (whence) {
			case ABSOLUTE:
				this.cursor = i;
				break;
			case RELATIVE:
				this.cursor += i;
				break;
			case END:
				this.cursor = this.value.length() + i;
		}

		this.cursor = Mth.clamp(this.cursor, 0, this.value.length());
		if (!this.selecting && !Minecraft.getInstance().hasShiftDown()) {
			this.selectCursor = this.cursor;
		}
	}

	protected void moveCursorY(int direction) {
		if (direction != 0) {
			int j = TextBoxStringUtils.width(this.font, this.value.substring(this.getCursorLineView().start(), this.cursor)) + 2;
			MultilineStringView stringView = this.getCursorLineView(direction);
			int k = this.font.plainSubstrByWidth(this.value.substring(stringView.start(), stringView.end()), j).length();
			this.seekCursor(Whence.ABSOLUTE, stringView.start() + k);
		}
	}

	public List<MultilineStringView> lines(int width) {
		if (this.lastWidth != width) {
			this.lastWidth = width;
			this.reflowDisplayLines();
		}
		return this.lines;
	}

	public boolean hasSelection() {
		return this.selectCursor != this.cursor;
	}

	protected MultilineStringView getCursorLineView() {
		return this.getCursorLineView(0);
	}

	private MultilineStringView getCursorLineView(int i) {
		int line = this.getLineAtCursor();
		if (line < 0) {
			throw new IllegalStateException("Cursor is not within text (cursor = " + this.cursor + ", length = " + this.value.length() + ")");
		}
		return this.lines.get(Mth.clamp(line + i, 0, this.lines.size() - 1));
	}

	protected int getPreviousWordStart() {
		if (this.value.isEmpty()) {
			return 0;
		} else {
			int i = Mth.clamp(this.cursor, 0, this.value.length() - 1);

			while (i > 0 && Character.isWhitespace(this.value.charAt(i - 1))) {
				i--;
			}

			while (i > 0 && !Character.isWhitespace(this.value.charAt(i - 1))) {
				i--;
			}

			return i;
		}
	}

	protected int getNextWordStart() {
		if (this.value.isEmpty()) {
			return 0;
		} else {
			int start = Mth.clamp(this.cursor, 0, this.value.length() - 1);

			while (start < this.value.length() && !Character.isWhitespace(this.value.charAt(start))) {
				start++;
			}

			while (start < this.value.length() && Character.isWhitespace(this.value.charAt(start))) {
				start++;
			}

			return start;
		}
	}

	private void reflowDisplayLines() {
		this.lines.clear();
		if (this.value.isEmpty()) {
			this.lines.add(MultilineStringView.EMPTY);
		} else {
			var lines = this.value.split("\n", Integer.MAX_VALUE);

			int x = 0;

			for (var i = 0; i < lines.length; i++) {
				final var index = i;
				final var line = lines[i];
				final var xOffset = x;

				if (line.isEmpty()) {
					this.lines.add(new MultilineStringView(index, xOffset, xOffset));
				} else {
					TextBoxStringUtils.split(
							this.font, line, this.lastWidth,
							(style, start, end) -> this.lines.add(new MultilineStringView(index, xOffset + start, xOffset + end))
					);
				}

				x += line.length() + 1;
			}
		}
	}
}