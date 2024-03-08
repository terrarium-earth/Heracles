package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.components.widgets.item.ItemButton;

public record QuestIconSetting() implements Setting<ItemQuestIcon, ItemButton> {
    public static final QuestIconSetting INSTANCE = new QuestIconSetting();

    @Override
    public ItemButton createWidget(ItemButton old, int width, ItemQuestIcon value) {
        return new ItemButton(old, value.item().copy(), width, 24, true);
    }

    @Override
    public ItemQuestIcon getValue(ItemButton widget) {
        return new ItemQuestIcon(widget.reference().get());
    }
}
