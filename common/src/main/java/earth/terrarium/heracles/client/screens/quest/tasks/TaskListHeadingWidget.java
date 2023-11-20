package earth.terrarium.heracles.client.screens.quest.tasks;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public record TaskListHeadingWidget(int tasks, int completed) implements DisplayWidget {

    private static final String DESC_SINGULAR = "task.heracles.progress.desc.incomplete.singular";
    private static final String DESC_PLURAL = "task.heracles.progress.desc.incomplete.plural";
    private static final String DESC_COMPLETE = "task.heracles.progress.desc.complete";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        WidgetUtils.drawSummaryBackground(graphics, x, y, width, 30);

        String desc = tasks == completed ? DESC_COMPLETE : (tasks - completed > 1 ? DESC_PLURAL : DESC_SINGULAR);
        String completion = String.format("%.0f%%", this.completed * 100 / (double) tasks);

        graphics.drawString(
            Minecraft.getInstance().font,
            ConstantComponents.Tasks.PROGRESS, x + 5, y + 5, QuestScreenTheme.getSummaryTitle(),
            false
        );
        graphics.drawString(
            Minecraft.getInstance().font,
            completion, x + width - 5 - Minecraft.getInstance().font.width(completion), y + 5, QuestScreenTheme.getSummaryProgress(),
            false
        );
        graphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable(desc, tasks - completed), x + 5, y + 25 - Minecraft.getInstance().font.lineHeight, QuestScreenTheme.getSummaryDescription(),
            false
        );
    }

    @Override
    public int getHeight(int width) {
        return 30;
    }
}
