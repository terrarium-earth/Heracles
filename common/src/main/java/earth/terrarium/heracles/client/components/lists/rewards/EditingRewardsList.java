package earth.terrarium.heracles.client.components.lists.rewards;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.components.lists.rewards.entries.EditQuestRewardEntry;
import earth.terrarium.heracles.client.components.lists.ListEntry;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.ServerboundResetProgressPacket;
import org.jetbrains.annotations.Nullable;

public class EditingRewardsList extends RewardsList {

    public EditingRewardsList(@Nullable QuestList<QuestReward<?>> list, int width, int height, QuestContent content) {
        super(list, width, height, content);
    }

    @Override
    public ListEntry<QuestReward<?>> create(QuestReward<?> reward, DisplayWidget widget) {
        return new EditQuestRewardEntry(this, reward, widget);
    }

    @Override
    public void resetProgress(QuestReward<?> reward) {
        this.content().progress().claimedRewards().remove(reward.id());
        NetworkHandler.CHANNEL.sendToServer(new ServerboundResetProgressPacket(this.content().id(), reward.id(), false));
        update();
    }
}
