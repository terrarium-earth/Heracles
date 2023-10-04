package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.widgets.buttons.IconButton;

public record QuestIconSetting() implements Setting<ItemQuestIcon, IconButton> {
    public static final QuestIconSetting INSTANCE = new QuestIconSetting();

    @Override
    public IconButton createWidget(int width, ItemQuestIcon value) {
        return new IconButton(0, 0, width, 11, value);
    }

    @Override
    public ItemQuestIcon getValue(IconButton widget) {
        return widget.value();
    }
}
