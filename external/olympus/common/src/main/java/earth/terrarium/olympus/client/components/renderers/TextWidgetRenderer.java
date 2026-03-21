package earth.terrarium.olympus.client.components.renderers;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.utils.OlympusUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class TextWidgetRenderer<T extends AbstractWidget> implements WidgetRenderer<T>, ColorableWidget {

    private final Component text;
    private Font font = Minecraft.getInstance().font;
    private Color color = MinecraftColors.DARK_GRAY;
    private boolean drawShadow = false;

    private float alignX = 0.5f;

    TextWidgetRenderer(Component text) {
        this.text = text;
    }

    @Override
    public void render(GuiGraphics graphics, WidgetRendererContext<T> context, float partialTick) {
        int textWidth = this.font.width(this.text);
        int centerY = context.getY() + context.getHeight() / 2 - font.lineHeight / 2;
        double seconds = (double) Util.getMillis() / 1000.0;
        if (textWidth > context.getWidth()) {
            int overhang = textWidth - context.getWidth();
            double e = Math.max((double) overhang * 0.5, 3.0);
            double f = Math.sin(Mth.HALF_PI * Math.cos(Mth.TWO_PI * seconds / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, overhang);
            graphics.enableScissor(context.getLeft(), context.getTop(), context.getRight(), context.getBottom());
            graphics.drawString(this.font, this.text, context.getX() - (int) g, centerY, OlympusUtils.getEnsureAlpha(color), this.drawShadow);
            graphics.disableScissor();
        } else {
            int centerX = context.getX() + Math.round(context.getWidth() * alignX);
            graphics.drawString(this.font, this.text, centerX - Math.round(textWidth * alignX), centerY, OlympusUtils.getEnsureAlpha(color), this.drawShadow);
        }
    }

    public TextWidgetRenderer<T> withShadow() {
        this.drawShadow = true;
        return this;
    }

    public TextWidgetRenderer<T> withColor(Color color) {
        this.color = color;
        return this;
    }

    public TextWidgetRenderer<T> withFont(Font font) {
        this.font = font;
        return this;
    }

    public TextWidgetRenderer<T> withAlignment(float alignX) {
        this.alignX = alignX;
        return this;
    }

    public TextWidgetRenderer<T> withLeftAlignment() {
        this.alignX = 0;
        return this;
    }

    public TextWidgetRenderer<T> withCenterAlignment() {
        this.alignX = 0.5f;
        return this;
    }

    public TextWidgetRenderer<T> withRightAlignment() {
        this.alignX = 1;
        return this;
    }
}