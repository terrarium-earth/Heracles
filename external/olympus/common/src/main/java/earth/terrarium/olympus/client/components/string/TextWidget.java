package earth.terrarium.olympus.client.components.string;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import earth.terrarium.olympus.client.components.renderers.ColorableWidget;
import earth.terrarium.olympus.client.components.renderers.TextWidgetRenderer;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class TextWidget extends BaseWidget implements ColorableWidget {

    protected final Component text;
    protected TextWidgetRenderer<TextWidget> renderer;

    public TextWidget(Component text) {
        super();
        this.text = text;
        this.renderer = WidgetRenderers.text(text);
        Font font = Minecraft.getInstance().font;
        this.height = font.lineHeight;
        this.width = font.width(text);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderer.render(graphics, new WidgetRendererContext<>(this, mouseX, mouseY), partialTick);
    }

    @Override
    public TextWidget withColor(Color color) {
        this.renderer = this.renderer.withColor(color);
        return this;
    }

    @Override
    public TextWidget withShadow() {
        this.renderer = this.renderer.withShadow();
        return this;
    }

    public TextWidget withFont(Font font) {
        this.renderer = this.renderer.withFont(font);
        return this;
    }

    public TextWidget withAlignment(float alignX) {
        this.renderer = this.renderer.withAlignment(alignX);
        return this;
    }

    public TextWidget withLeftAlignment() {
        return this.withAlignment(0);
    }

    public TextWidget withCenterAlignment() {
        return this.withAlignment(0.5f);
    }

    public TextWidget withRightAlignment() {
        return this.withAlignment(1);
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return null;
    }
}
