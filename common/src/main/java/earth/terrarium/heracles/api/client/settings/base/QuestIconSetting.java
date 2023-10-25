package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.widgets.buttons.ItemButton;
import earth.terrarium.heracles.common.utils.ItemValue;

public record QuestIconSetting() implements Setting<ItemQuestIcon, ItemButton> {
    public static final QuestIconSetting INSTANCE = new QuestIconSetting();

    @Override
    public ItemButton createWidget(int width, ItemQuestIcon value) {
        return new ItemButton(0, 0, width, 11, true, value.item().item());
    }

    @Override
    public ItemQuestIcon getValue(ItemButton widget) {
        return new ItemQuestIcon(new ItemValue(widget.value()));
    }
}
