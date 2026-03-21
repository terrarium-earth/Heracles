package earth.terrarium.heracles.client.ui.quest;

import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.components.lists.rewards.RewardsList;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;

import java.util.concurrent.atomic.AtomicInteger;

public class RewardsQuestScreen extends AbstractQuestScreen {

    private QuestList<QuestReward<?>> list = null;

    public RewardsQuestScreen(Screen parent, QuestContent content) {
        super(parent, content, QuestTab.REWARDS);
    }

    @Override
    protected GridLayout initContent(AtomicInteger row) {
        GridLayout layout = super.initContent(row);
        this.list = layout.addChild(
            new RewardsList(this.list, this.contentWidth - 40, this.contentHeight, this.content),
            row.getAndIncrement(), 0,
            LayoutSettings.defaults().paddingHorizontal(20).paddingVertical(5)
        );
        return layout;
    }
}
