package earth.terrarium.heracles.client.screens.quest;

import earth.terrarium.heracles.client.tags.SubtitleTagElement;
import earth.terrarium.heracles.client.tags.WidgetTagElement;
import earth.terrarium.hermes.api.DefaultTagProvider;

public class QuestTagProvider extends DefaultTagProvider {

    public QuestTagProvider() {
        super();
        addSerializer("subtitle", SubtitleTagElement::new);
        addSerializer("task", WidgetTagElement::ofTask);
        addSerializer("reward", WidgetTagElement::ofReward);
    }
}
