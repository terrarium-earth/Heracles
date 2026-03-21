package earth.terrarium.olympus.client.components.string;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractStringWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultilineTextWidget extends AbstractStringWidget {
	protected float alignX = 0.5f;
	protected float textAlign = 0f;
	protected boolean shadow;
	protected float scale = 1.0f;

	protected List<FormattedCharSequence> lines;
	protected int maxLineWidth;
    protected int color = -1;

	protected List<Consumer<Style>> styleActions = new ArrayList<>();

	public MultilineTextWidget(int width, Component component, Font font) {
		super(0, 0, width, 0, component, font);

		this.lines = font.split(component, width);
		this.height = font.lineHeight * this.lines.size();
		this.maxLineWidth = this.lines.stream().mapToInt(font::width).max().orElse(0);
	}

	public MultilineTextWidget(Component text, int width) {
		this(width, text, Minecraft.getInstance().font);
	}

	public static MultilineTextWidget create(int width, Component text) {
		return new MultilineTextWidget(width, text, Minecraft.getInstance().font);
	}

	public MultilineTextWidget setColor(int color) {
        this.color = color;
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

	public @NotNull MultilineTextWidget textAlignLeft() {
		this.textAlign = 0.0F;
		return this;
	}

	public @NotNull MultilineTextWidget textAlignCenter() {
		this.textAlign = 0.5F;
		return this;
	}

	public @NotNull MultilineTextWidget textAlignRight() {
		this.textAlign = 1.0F;
		return this;
	}

	public @NotNull MultilineTextWidget shadow() {
		this.shadow = true;
		return this;
	}

	public @NotNull MultilineTextWidget scale(float scale) {
		this.scale = scale;
		var inverseScale = 1.0f / this.scale;

		this.lines = this.getFont().split(this.getMessage(), (int) Math.ceil(width * inverseScale));
		this.height = (int) Math.ceil(getFont().lineHeight * this.lines.size() * scale);
		this.maxLineWidth = this.lines.stream().mapToInt(it -> (int)  Math.ceil(getFont().width(it) * scale)).max().orElse(0);
		return this;
	}

	public @NotNull MultilineTextWidget clickActionCallback(Consumer<Style> action) {
		this.styleActions.add(action);
		return this;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		Font font = this.getFont();

		int x = this.getX() + Math.round(this.alignX * (float)(this.getWidth() - maxLineWidth));
		int y = this.getY();

		var pose = graphics.pose();
		pose.pushMatrix();
		pose.translate(x, y);
		pose.scale(this.scale, this.scale);

		y = 0;

		var invertedScale = 1.0f / this.scale;
		var adjustedMaxLineWidth = (int) Math.ceil(maxLineWidth * invertedScale);

		for (FormattedCharSequence line : this.lines) {
			var xOffset = (int) Math.ceil((adjustedMaxLineWidth - font.width(line)) * textAlign);
			graphics.drawString(font, line, xOffset, y, this.getColor(), this.shadow);
			y += font.lineHeight;
		}
		pose.popMatrix();

		Style style = getStyle(mouseX, mouseY);
		if (style != null && style.getClickEvent() != null) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
		}
	}

    @Override
    public void visitLines(@NotNull ActiveTextCollector collector) {
    }

    @Nullable
	public Style getStyle(double mouseX, double mouseY) {
		if (!isMouseOver(mouseX, mouseY)) return null;
		Font font = this.getFont();

		float y = this.getY();
		int lineIndex = 0;

		while (!(mouseY >= y && mouseY <= y + font.lineHeight * scale)) {
			y += font.lineHeight * scale;
			lineIndex++;
		}

		float x = this.getX() + (this.alignX * (float)(this.getWidth() - maxLineWidth));
		float lineWidth = font.width(this.lines.get(lineIndex)) * scale;
		x += (maxLineWidth - lineWidth) * textAlign;
		if (!(mouseX >= x && mouseX <= x + lineWidth)) return null;
        return StringUtils.getStyleAt(font, this.lines.get(lineIndex), (int) ((mouseX - x) * (1f / scale)));
	}

    @Override
    public void onClick(MouseButtonEvent event, boolean bl) {
        var style = getStyle(event.x(), event.y());
        if (style == null) return;
        if (style.getClickEvent() != null) {
            for (Consumer<Style> styleAction : styleActions) {
                styleAction.accept(style);
            }
        }
    }

    public int getColor() {
        return ARGB.color(this.alpha, this.color);
    }
}