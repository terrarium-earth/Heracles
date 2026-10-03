package earth.terrarium.heracles.client.tags;

import earth.terrarium.hermes.api.rendering.HtmlRenderer;
import earth.terrarium.hermes.api.rendering.HtmlStyle;
import earth.terrarium.hermes.libs.minemark.LayoutStyle;
import earth.terrarium.hermes.libs.minemark.elements.Element;
import earth.terrarium.hermes.libs.minemark.elements.impl.LinkElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;

public class URLTooltipLinkElement extends LinkElement<HtmlStyle, HtmlRenderer> {

    public URLTooltipLinkElement(@NotNull HtmlStyle style, @NotNull LayoutStyle layoutStyle, @Nullable Element<HtmlStyle, HtmlRenderer> parent, @NotNull String qName, @Nullable Attributes attributes) {
        super(style, layoutStyle, parent, qName, attributes);
    }

    @Override
    public void drawInternal(float xOffset, float yOffset,
                             float mouseX, float mouseY,
                             HtmlRenderer renderer) {
        super.drawInternal(xOffset, yOffset, mouseX, mouseY, renderer);

        if (link != null && isAnyInside(mouseX, mouseY)) {
            renderer.setTooltip(Component.literal(link));
        }
    }
}