package earth.terrarium.heracles.client.tags;


import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.hermes.api.rendering.HtmlRenderer;
import earth.terrarium.hermes.api.rendering.HtmlStyle;
import earth.terrarium.hermes.elements.custom.HermesText;
import earth.terrarium.hermes.libs.minemark.LayoutData;
import earth.terrarium.hermes.libs.minemark.LayoutStyle;
import earth.terrarium.hermes.libs.minemark.elements.BasicElement;
import earth.terrarium.hermes.libs.minemark.elements.Element;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.xml.sax.Attributes;

public class SubtitleElement extends BasicElement<HtmlStyle, HtmlRenderer> {

    private final Component subtitle;

    public SubtitleElement(HtmlStyle style, LayoutStyle layoutStyle, @Nullable Element<HtmlStyle, HtmlRenderer> parent, String qName, @Nullable Attributes attributes) {
        super(style, layoutStyle, parent, qName, attributes);

        if (attributes != null) {
            @Nullable String id = attributes.getValue("id");

            if (id != null) {
                this.subtitle = ClientQuests.get(id)
                    .map(ClientQuests.QuestEntry::value)
                    .map(Quest::display)
                    .map(QuestDisplay::subtitle)
                    .orElse(CommonComponents.EMPTY);
            } else {
                this.subtitle = null;
            }

            new HermesText<HtmlStyle, HtmlRenderer>().applyStyle(style, layoutStyle, this, qName, attributes);
        } else {
            this.subtitle = null;
        }
    }

    @Override
    protected void drawElement(float x, float y, float width, float height, HtmlRenderer renderData) {
        if (subtitle != null) {
            renderData.setTooltip(subtitle);
        }
    }

    @Override
    protected float getWidth(LayoutData layoutData, HtmlRenderer renderData) {
        // TODO
        return 0;
    }

    @Override
    protected float getHeight(LayoutData layoutData, HtmlRenderer renderData) {
        return 0;
    }
}
