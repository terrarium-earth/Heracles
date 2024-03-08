package earth.terrarium.heracles.client.components.string;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultilineTextWidget extends AbstractStringWidget implements CursorWidget {

	protected float alignX = 0.5f;
	protected boolean shadow;

	private final List<FormattedCharSequence> lines;
	private final int maxLineWidth;

	public MultilineTextWidget(int width, Component component, Font font) {
		super(0, 0, width, 0, component, font);

		this.lines = font.split(component, width);
		this.height = font.lineHeight * this.lines.size();
		this.maxLineWidth = this.lines.stream().mapToInt(font::width).max().orElse(0);
	}

	public MultilineTextWidget setColor(int color) {
		super.setColor(color);
		return this;
	}

	public @NotNull MultilineTextWidget alignLeft() {
		this.alignX = 0.0F;
		return this;
	}

	public @NotNull MultilineTextWidget alignCenter() {
		this.alignX = 0.5F;
		return this;
	}

	public @NotNull MultilineTextWidget alignRight() {
		this.alignX = 1.0F;
		return this;
	}

	public @NotNull MultilineTextWidget shadow() {
		this.shadow = true;
		return this;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		Font font = this.getFont();

		int x = this.getX() + Math.round(this.alignX * (float)(this.getWidth() - maxLineWidth));
		int y = this.getY();

		for (FormattedCharSequence line : this.lines) {
			graphics.drawString(font, line, x, y, this.getColor(), this.shadow);
			y += font.lineHeight;
		}
	}

	@Override
	public CursorScreen.Cursor getCursor() {
		return CursorScreen.Cursor.DEFAULT;
	}
}