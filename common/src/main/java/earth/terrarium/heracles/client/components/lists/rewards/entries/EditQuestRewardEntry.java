package earth.terrarium.heracles.client.components.lists.rewards.entries;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets;
import earth.terrarium.heracles.client.components.lists.AbstractEditListEntry;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;

public final class EditQuestRewardEntry extends AbstractEditListEntry<QuestReward<?>> {

    public EditQuestRewardEntry(QuestList<QuestReward<?>> list, QuestReward<?> reward, DisplayWidget widget) {
        super(list, reward, widget);
    }

    @Override
    protected void setValue(QuestReward<?> reward) {
        DisplayWidget widget = QuestRewardWidgets.create(ModUtils.cast(reward));
        if (widget != null) {
            this.value = reward;
            this.widget = QuestRewardWidgets.create(ModUtils.cast(reward));
        }
        ClientQuests.updateQuest(entry(), quest -> {
            quest.rewards().put(reward.id(), reward);
            return NetworkQuestData.builder().rewards(quest.rewards());
        });
    }

    @Override
    protected void edit() {
        EditObjectModal.open(ModUtils.cast(this.value.type()), ConstantComponents.Rewards.EDIT, this.value.id(), this.value, this::setValue);
    }

    @Override
    protected void delete() {
        ClientQuests.updateQuest(entry(), quest -> {
            quest.rewards().remove(value.id());
            return NetworkQuestData.builder().rewards(quest.rewards());
        });
        this.list.update();
    }

    @Override
    public String id() {
        return this.value.id();
    }
}
