package earth.terrarium.heracles.client.screens.quest;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.screens.quest.rewards.RewardListWidget;
import earth.terrarium.heracles.client.screens.quest.tasks.TaskListWidget;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.hermes.api.TagProvider;
import earth.terrarium.hermes.api.themes.DefaultTheme;
import earth.terrarium.hermes.client.DocumentWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

public class QuestScreen extends BaseQuestScreen {

    private TaskListWidget taskList;
    private RewardListWidget rewardList;
    private DocumentWidget description;

    private String descriptionError;
    private String desc;

    public QuestScreen(QuestContent content) {
        super(content);
    }

    @Override
    public void updateProgress(@Nullable QuestProgress newProgress) {
        super.updateProgress(newProgress);
        this.taskList.update(this.quest().tasks().values());
        this.rewardList.update(this.content.fromGroup(), this.content.id(), this.quest());
        // Completely recreate description in case of task/reward tags
        // Can't touch hermes to make a better solution
        int contentX = (int) (this.width * 0.31f);
        int contentY = 30;
        int contentWidth = (int) (this.width * 0.63f);
        int contentHeight = this.height - 45;
        if (this.overview == null) {
            contentX = (int) ((this.width - contentWidth) / 2f);
        }
        TagProvider provider = new QuestTagProvider();
        this.description = new DocumentWidget(contentX, contentY, contentWidth, contentHeight, new DefaultTheme(), provider.parse(desc));
    }

    @Override
    protected void init() {
        super.init();
        int contentX = (int) (this.width * 0.31f);
        int contentY = 30;
        int contentWidth = (int) (this.width * 0.63f);
        int contentHeight = this.height - 45;

        if (this.overview == null) {
            contentX = (int) ((this.width - contentWidth) / 2f);
        }
        this.taskList = new TaskListWidget(contentX, contentY, contentWidth, contentHeight, this.content.id(), this.entry(), this.content.progress(), this.content.quests(), null, null);
        this.rewardList = new RewardListWidget(contentX, contentY, contentWidth, contentHeight, this.entry(), this.content.progress(), null, null);
        try {
            this.descriptionError = null;
            this.desc = String.join("", MarkdownParser.parse(this.quest().display().description()));
        } catch (Throwable e) {
            this.descriptionError = e.getMessage();
            Heracles.LOGGER.error("Error parsing quest description: ", e);
        }
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasPermissions(2)) {
            addRenderableWidget(new ImageButton(this.width - 24, 1, 11, 11, 33, 15, 11, HEADING, 256, 256, (button) ->
                NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(this.content.fromGroup(), this.content.id(), true))
            )).setTooltip(Tooltip.create(ConstantComponents.TOGGLE_EDIT));
        }
        updateProgress(null);
    }

    @Override
    public GuiEventListener getTaskList() {
        return this.taskList;
    }

    @Override
    public GuiEventListener getRewardList() {
        return this.rewardList;
    }

    @Override
    public GuiEventListener getDescriptionWidget() {
        return this.description;
    }

    @Override
    public String getDescriptionError() {
        return this.descriptionError;
    }
}
