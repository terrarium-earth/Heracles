package earth.terrarium.heracles.client.components.string;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextWidget extends AbstractStringWidget implements CursorWidget {

	protected float alignX = 0.5f;
	protected float alignY = 0.5f;
	protected boolean shadow;

	public TextWidget(int width, int height, Component component, Font font) {
		super(0, 0, width, height, component, font);
	}

	public TextWidget setColor(int color) {
		super.setColor(color);
		return this;
	}

	public @NotNull TextWidget alignLeft() {
		this.alignX = 0.0F;
		return this;
	}

	public @NotNull TextWidget alignCenter() {
		this.alignX = 0.5F;
		return this;
	}

	public @NotNull TextWidget alignRight() {
		this.alignX = 1.0F;
		return this;
	}

	public @NotNull TextWidget alignTop() {
		this.alignY = 0.0F;
		return this;
	}

	public @NotNull TextWidget alignMiddle() {
		this.alignY = 0.5F;
		return this;
	}

	public @NotNull TextWidget alignBottom() {
		this.alignY = 1.0F;
		return this;
	}

	public @NotNull TextWidget shadow() {
		this.shadow = true;
		return this;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		Component component = this.getMessage();
		Font font = this.getFont();
		int x = this.getX() + Math.round(this.alignX * (float)(this.getWidth() - font.width(component)));
		int y = this.getY() + Math.round(this.alignY * (float)(this.getHeight() - font.lineHeight));
		graphics.drawString(font, component, x, y, this.getColor(), this.shadow);
	}

	@Override
	public CursorScreen.Cursor getCursor() {
		return CursorScreen.Cursor.DEFAULT;
	}
}