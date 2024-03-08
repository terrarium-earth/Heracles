package earth.terrarium.heracles.client.components.quest;

import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class QuestInfo extends BaseWidget {

    private static final int PADDING = 5;
    private static final int LINE_HEIGHT = Minecraft.getInstance().font.lineHeight;
    private static final int SECTION_HEIGHT = LINE_HEIGHT + PADDING * 2;

    private final QuestProgress progress;
    private final Quest quest;

    public QuestInfo(int width, String id) {
        super(width, SECTION_HEIGHT * 2 - 1);
        this.progress = ClientQuests.getProgress(id);
        this.quest = ClientQuests.getQuest(id).orElse(null);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.quest == null) return;

        int tasks = this.quest.tasks().size();
        int completed = (int) this.quest.tasks().values().stream().filter(t -> progress.getTask(t).isComplete()).count();
        int rewards = this.quest.rewards().size();
        int claimed = this.progress.claimedRewards().size();

        Component tasksText = Component.literal(completed + "/" + tasks);
        Component rewardsText = Component.literal(claimed + "/" + rewards);


        Font font = Minecraft.getInstance().font;
        UIConstants.blitWithEdge(graphics, UIConstants.REWARD_OVERVIEW, getX(), getY(), this.width, SECTION_HEIGHT, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.REWARD_OVERVIEW, getX(), getY() + SECTION_HEIGHT - 1, this.width, SECTION_HEIGHT, 2);

        int left = getX() + PADDING;
        int right = getX() + this.width - PADDING;
        int top = getY() + PADDING;

        graphics.drawString(font, ConstantComponents.Tasks.TITLE, left, top, QuestScreenTheme.getSummaryDescription(), false);
        UIUtils.rightAlign(graphics, font, tasksText, right, top, QuestScreenTheme.getSummaryProgress(), false);

        top += SECTION_HEIGHT - 1;

        graphics.drawString(font, ConstantComponents.Rewards.TITLE, left, top, QuestScreenTheme.getSummaryDescription(), false);
        UIUtils.rightAlign(graphics, font, rewardsText, right, top, QuestScreenTheme.getSummaryProgress(), false);
    }
}
