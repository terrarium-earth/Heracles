package earth.terrarium.olympus.client.components.renderers;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;

public class TextWithIconWidgetRenderer<T extends AbstractWidget> implements WidgetRenderer<T>, ColorableWidget {
    private int gap = 4;
    private final TextWidgetRenderer<T> textRenderer;
    private final IconWidgetRenderer<T> iconRenderer;

    private int iconAlign = 1;
    private int iconSize = 10;

    TextWithIconWidgetRenderer(TextWidgetRenderer<T> text, IconWidgetRenderer<T> icon) {
        this.textRenderer = text;
        text.withAlignment(0);
        this.iconRenderer = icon;
    }

    @Override
    public void render(GuiGraphics graphics, WidgetRendererContext<T> widget, float partialTick) {
        var iconSpace = iconSize + gap;
        textRenderer.withPadding(0, iconAlign * iconSpace, 0, (1 - iconAlign) * iconSpace).render(graphics, widget, partialTick);
        var textSpace = widget.getWidth() - iconSpace;
        iconRenderer.withCentered(iconSize, iconSize).withPadding(0, (1 - iconAlign) * textSpace, 0, iconAlign * textSpace).render(graphics, widget, partialTick);
    }

    public TextWithIconWidgetRenderer<T> withGap(int gap) {
        this.gap = gap;
        return this;
    }

    public TextWithIconWidgetRenderer<T> withShadow() {
        textRenderer.withShadow();
        iconRenderer.withShadow();
        return this;
    }

    public TextWithIconWidgetRenderer<T> withColor(Color color) {
        textRenderer.withColor(color);
        iconRenderer.withColor(color);
        return this;
    }

    public TextWithIconWidgetRenderer<T> withIconSize(int size) {
        iconSize = size;
        return this;
    }

    public TextWithIconWidgetRenderer<T> withFont(Font font) {
        textRenderer.withFont(font);
        return this;
    }

    public TextWithIconWidgetRenderer<T> withRightAlignedIcon() {
        iconAlign = 1;
        return this;
    }

    public TextWithIconWidgetRenderer<T> withLeftAlignedIcon() {
        iconAlign = 0;
        return this;
    }

    public TextWithIconWidgetRenderer<T> withTextLeftIconRight() {
        textRenderer.withAlignment(0);
        iconAlign = 1;
        return this;
    }

    public TextWithIconWidgetRenderer<T> withTextRightIconLeft() {
        textRenderer.withAlignment(1);
        iconAlign = 0;
        return this;
    }

    public TextWithIconWidgetRenderer<T> withTextLeftIconLeft() {
        textRenderer.withAlignment(0);
        iconAlign = 0;
        return this;
    }

    public TextWithIconWidgetRenderer<T> withTextRightIconRight() {
        textRenderer.withAlignment(1);
        iconAlign = 1;
        return this;
    }

    public TextWithIconWidgetRenderer<T> withTextAlignment(float alignment) {
        textRenderer.withAlignment(alignment);
        return this;
    }
}
