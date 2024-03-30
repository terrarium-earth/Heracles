package earth.terrarium.heracles.client.screens.quests;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.api.client.theme.QuestsScreenTheme;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.screens.mousemode.MouseMode;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.widgets.modals.ConfirmModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardsPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestsScreen extends AbstractQuestScreen<QuestsContent> {

    protected SelectQuestWidget selectQuestWidget;
    protected QuestsWidget questsWidget;
    protected GroupsList groupsList;

    protected ConfirmModal confirmModal;

    public QuestsScreen(QuestsContent content) {
        super(content, CommonComponents.EMPTY);
        this.hasBackButton = false;
    }

    @Override
    protected void init() {
        super.init();
        if (QuestTab.canEdit()) {
            addRenderableWidget(new ImageButton(this.width - 24, 1, 11, 11, 33, 15, 11, HEADING, 256, 256, (button) ->
                NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(this.content.group()))
            )).setTooltip(Tooltip.create(ConstantComponents.TOGGLE_EDIT));
        }

        if (!(this instanceof QuestsEditScreen)) {
            addRenderableWidget(new ImageButton(sideBarWidth + 3, 1, 11, 11, 44, 15, 11, HEADING, 256, 256, (button) -> {
                NetworkHandler.CHANNEL.sendToServer(new ClaimRewardsPacket());
            })).setTooltip(Tooltip.create(ConstantComponents.Rewards.CLAIM));
        }

        List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> quests = new ArrayList<>();
        content.quests().forEach((id, status) ->
            ClientQuests.get(id)
                .filter(quest -> quest.value().display().groups().containsKey(content.group()))
                .ifPresent(quest -> quests.add(Pair.of(quest, status)))
        );

        questsWidget = addRenderableWidget(new QuestsWidget(
            (int) (this.width * 0.25f), // aka SIDE_BAR_PORTION
            15,
            (int) (this.width * 0.75f),
            (int) (this.width * 0.50f),
            this.height - 15,
            () -> actualChildren().contains(selectQuestWidget),
            this::getMouseMode,
            quest -> {
                if (selectQuestWidget != null) {
                    if (quest == null) {
                        removeWidget(selectQuestWidget);
                    } else if (!actualChildren().contains(selectQuestWidget)) {
                        addRenderableWidget(selectQuestWidget);
                    }
                    selectQuestWidget.setEntry(quest);
                }
            }
        ));
        questsWidget.update(this.content, quests);
        HeraclesClient.lastGroup = content.group();

        this.groupsList = addRenderableWidget(new GroupsList(
            0,
            15,
            sideBarWidth,
            this.height - 15,
            entry -> {
                if (entry == null || this.content.group().equals(entry.name())) return;
                NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(entry.name()));
            }
        ));
        this.groupsList.update(ClientQuests.groups(), this.content.group());

        this.confirmModal = addTemporary(new ConfirmModal(this.width, this.height));
    }

    protected MouseMode getMouseMode() {
        return MouseMode.DRAG_MOVE_OPEN;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!isTemporaryWidgetVisible() && questsWidget != null && questsWidget.isMouseOver(mouseX, mouseY)) {
            questsWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        int center = sideBarWidth / 2;
        int textX = center - font.width(ConstantComponents.Groups.GROUPS) / 2;
        graphics.drawString(
            font,
            ConstantComponents.Groups.GROUPS, textX, 3, QuestsScreenTheme.getHeaderGroupsTitle(),
            false
        );
    }

    public GroupsList getGroupsList() {
        return groupsList;
    }

    public ConfirmModal confirmModal() {
        return confirmModal;
    }

    public String getGroup() {
        return content.group();
    }

    public void updateProgress(Map<String, QuestProgress> quests) {
        List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> statues = new ArrayList<>();
        for (var entry : this.content.quests().entrySet()) {
            ClientQuests.QuestEntry quest = ClientQuests.get(entry.getKey())
                .filter(q -> q.value().display().groups().containsKey(content.group()))
                .orElse(null);
            if (quest == null) continue;
            if (entry.getValue() == ModUtils.QuestStatus.COMPLETED) {
                QuestProgress progress = quests.get(entry.getKey());
                if (progress != null && progress.isClaimed(quest.value())) {
                    entry.setValue(ModUtils.QuestStatus.COMPLETED_CLAIMED);
                }
            }
            statues.add(Pair.of(quest, entry.getValue()));
        }
        if (questsWidget == null) return;
        questsWidget.update(this.content, statues);
    }
}
