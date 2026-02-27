package earth.terrarium.heracles.client.components.lists.rewards;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.components.lists.HeadingListEntry;
import earth.terrarium.heracles.client.components.lists.ListEntry;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.components.lists.QuestValueEntry;
import earth.terrarium.heracles.client.components.lists.rewards.entries.DependentRewardEntry;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RewardsList extends QuestList<QuestReward<?>> {

    private static final ListEntry<QuestReward<?>> LOCKED = new HeadingListEntry<>(Component.translatable("quest.heracles.locked"), UIConstants.LOCKED_HEADING);
    private static final ListEntry<QuestReward<?>> AVAILABLE = new HeadingListEntry<>(Component.translatable("quest.heracles.available"), UIConstants.CLAIMABLE_HEADING);
    private static final ListEntry<QuestReward<?>> CLAIMED = new HeadingListEntry<>(Component.translatable("quest.heracles.claimed"), UIConstants.CLAIMED_HEADING);
    private static final ListEntry<QuestReward<?>> DEPENDENTS = new HeadingListEntry<>(Component.literal("Dependents"), UIConstants.DEPENDENTS_HEADING);

    public RewardsList(@Nullable QuestList<QuestReward<?>> list, int width, int height, QuestContent content) {
        super(list, width, height, content);
    }

    public RewardsList(int width, int height, QuestContent content) {
        super(width, height, content);
    }

    @Override
    public ListEntry<QuestReward<?>> create(QuestReward<?> reward, DisplayWidget widget) {
        return new QuestValueEntry<>(reward, widget);
    }

    @Override
    public void update(String group) {
        ClientQuests.QuestEntry entry = ClientQuests.get(this.content().id()).orElse(null);
        if (entry == null) return;

        List<ListEntry<QuestReward<?>>> locked = new ArrayList<>();
        List<ListEntry<QuestReward<?>>> available = new ArrayList<>();
        List<ListEntry<QuestReward<?>>> claimed = new ArrayList<>();
        List<ListEntry<QuestReward<?>>> dependents = new ArrayList<>();

        for (var reward : entry.value().rewards().values()) {
            DisplayWidget widget = QuestRewardWidgets.create(reward);
            if (widget == null) continue;
            ListEntry<QuestReward<?>> rewardsEntry = create(reward, widget);
            if (this.content().progress().canClaim(reward.id())) {
                available.add(rewardsEntry);
            } else if (this.content().progress().isComplete()) {
                claimed.add(rewardsEntry);
            } else {
                locked.add(rewardsEntry);
            }
        }

        for (ClientQuests.QuestEntry child : entry.dependents()) {
            if (!child.value().display().groups().containsKey(group)) continue;
            dependents.add(new DependentRewardEntry(child.value()));
        }

        this.clear();
        if (!locked.isEmpty()) {
            this.add(LOCKED);
            locked.forEach(this::add);
        }
        if (!available.isEmpty()) {
            this.add(AVAILABLE);
            available.forEach(this::add);
        }
        if (!claimed.isEmpty()) {
            this.add(CLAIMED);
            claimed.forEach(this::add);
        }
        if (!dependents.isEmpty()) {
            this.add(DEPENDENTS);
            dependents.forEach(this::add);
        }
    }
}
