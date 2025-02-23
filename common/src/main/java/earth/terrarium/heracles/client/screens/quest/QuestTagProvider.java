package earth.terrarium.heracles.client.screens.quest;

import com.google.auto.service.AutoService;
import earth.terrarium.heracles.client.tags.SubtitleElement;
import earth.terrarium.heracles.client.tags.WidgetTagElement;
import earth.terrarium.hermes.api.ElementExtension;
import earth.terrarium.hermes.api.rendering.HtmlRenderer;
import earth.terrarium.hermes.api.rendering.HtmlStyle;
import dev.dediamondpro.minemark.MineMarkCoreBuilder;

@AutoService(ElementExtension.class)
public class QuestTagProvider implements ElementExtension {

    @Override
    public void addDefaultElements(MineMarkCoreBuilder<HtmlStyle, HtmlRenderer> builder) {
        builder.addElement("subtitle", SubtitleElement::new);
        builder.addElement("task", WidgetTagElement::ofTask);
        builder.addElement("reward", WidgetTagElement::ofReward);
    }
}
