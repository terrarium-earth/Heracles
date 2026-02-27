package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.DummyTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;

public record DummyTaskWidget(
    DummyTask task, TaskProgress<NumericTag> progress
) implements DisplayWidget {

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        int iconSize = 32;
        this.task.icon().render(graphics, x + 5, y + 5, iconSize, iconSize);
        graphics.drawString(
            font,
            task.titleOr(TaskTitleFormatter.create(this.task)), x + iconSize + 16, y + 6, QuestScreenTheme.getTaskTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(task.description()), x + iconSize + 16, y + 8 + font.lineHeight, QuestScreenTheme.getTaskDescription(),
            false
        );
        WidgetUtils.drawProgressText(graphics, x, y, width, this.task, this.progress);
        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 16, y + height - font.lineHeight - 5, x + width - 5, y + height - 6, this.task, this.progress);
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}
