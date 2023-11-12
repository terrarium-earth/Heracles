package earth.terrarium.heracles.client.screens.quest;

import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.client.utils.ThemeColors;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

public final class QuestProgressWidget implements Renderable {
    private static final String TITLE_INCOMPLETE = "gui.heracles.progress.title.incomplete";
    private static final String TITLE_COMPLETE = "gui.heracles.progress.title.complete";
    private static final String DESC_SINGULAR = "gui.heracles.progress.desc.incomplete.singular";
    private static final String DESC_PLURAL = "gui.heracles.progress.desc.incomplete.plural";
    private static final String DESC_COMPLETE_SINGULAR = "gui.heracles.progress.desc.complete.singular";
    private static final String DESC_COMPLETE_PLURAL = "gui.heracles.progress.desc.complete.plural";
    private static final String DESC_COMPLETE_CLAIMED = "gui.heracles.progress.desc.complete_claimed";
    private final int x;
    private final int y;
    private final int width;
    private int tasks = 0;
    private int completed = 0;
    private int rewards = 0;
    private int claimed = 0;

    public QuestProgressWidget(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public void update(int tasks, int completed, int rewards, int claimed) {
        this.tasks = tasks;
        this.completed = completed;
        this.rewards = rewards;
        this.claimed = claimed;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        WidgetUtils.drawStatusSummaryBackground(graphics, x, y, width, 30, tasks == completed ? (rewards == claimed ? ModUtils.QuestStatus.COMPLETED_CLAIMED : ModUtils.QuestStatus.COMPLETED) : ModUtils.QuestStatus.IN_PROGRESS);

        String title = tasks == completed ? TITLE_COMPLETE : TITLE_INCOMPLETE;
        String desc = tasks == completed ? (rewards == claimed ? DESC_COMPLETE_CLAIMED : (rewards - claimed > 1 ? DESC_COMPLETE_PLURAL : DESC_COMPLETE_SINGULAR)) : (tasks - completed > 1 ? DESC_PLURAL : DESC_SINGULAR);
        String completion = String.format("%.0f%%", this.completed * 100 / (double) tasks);

        graphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable(title), x + 5, y + 5, ThemeColors.SUMMARY_TITLE,
            false
        );
        graphics.drawString(
            Minecraft.getInstance().font,
            completion, x + width - 5 - Minecraft.getInstance().font.width(completion), y + 5, ThemeColors.SUMMARY_PROGRESS,
            false
        );
        graphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable(desc, tasks == completed ? rewards - claimed : tasks - completed), x + 5, y + 25 - Minecraft.getInstance().font.lineHeight, ThemeColors.SUMMARY_DESCRIPTION,
            false
        );
    }
}
