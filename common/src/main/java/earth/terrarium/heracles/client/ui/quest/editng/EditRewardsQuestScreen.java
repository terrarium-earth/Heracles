package earth.terrarium.heracles.client.ui.quest.editng;

import earth.terrarium.heracles.api.client.settings.Settings;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import earth.terrarium.heracles.client.components.lists.QuestList;
import earth.terrarium.heracles.client.components.lists.rewards.EditingRewardsList;
import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.quest.AbstractQuestScreen;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.modals.CreateObjectModal;
import earth.terrarium.heracles.client.ui.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EditRewardsQuestScreen extends AbstractQuestScreen {

    private QuestList<QuestReward<?>> list = null;

    public EditRewardsQuestScreen(Screen parent, QuestContent content) {
        super(parent, content, QuestTab.REWARDS);
    }

    @Override
    protected GridLayout initSidebar(AtomicInteger row) {
        addRenderableWidget(TextButton.create(
            this.sideBarWidth - SPACER - PADDING * 2, BUTTON_HEIGHT,
            Component.literal("Add Reward"),
            () -> CreateObjectModal.open("rewards", this::createReward, this::isValidCreation, getValidTypes())
        )).setPosition(PADDING, this.height - BUTTON_HEIGHT - PADDING);

        return super.initSidebar(row);
    }

    @Override
    protected GridLayout initContent(AtomicInteger row) {
        GridLayout layout = super.initContent(row);
        this.list = layout.addChild(
            new EditingRewardsList(this.list, this.contentWidth - 40, this.contentHeight, this.content),
            row.getAndIncrement(), 0,
            LayoutSettings.defaults().paddingHorizontal(20).paddingVertical(5)
        );
        return layout;
    }

    private void createReward(ResourceLocation type, String id) {
        EditObjectModal.open(QuestRewards.get(type), ConstantComponents.Rewards.EDIT, id, null, reward -> {
            ClientQuests.get(this.content().id()).ifPresent(entry -> ClientQuests.updateQuest(entry, quest -> {
                quest.rewards().put(id, reward);
                return NetworkQuestData.builder().rewards(quest.rewards());
            }));
            this.list.update(this.content().fromGroup());
        });
    }

    private boolean isValidCreation(ResourceLocation type, String id) {
        return !this.quest().rewards().containsKey(id) && type != null && QuestRewards.types().containsKey(type);
    }

    private List<ResourceLocation> getValidTypes() {
        return QuestRewards.types().values()
            .stream()
            .filter(Settings::hasFactory)
            .map(QuestRewardType::id)
            .toList();
    }
}
