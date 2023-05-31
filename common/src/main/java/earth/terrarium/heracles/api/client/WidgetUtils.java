package earth.terrarium.heracles.api.client;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.Tag;

public final class WidgetUtils {

    public static void drawBackground(GuiGraphics graphics, int x, int y, int width) {
        graphics.fill(x, y, x + width, y + (int) (width * 0.1f) + 10, 0x80808080);
        graphics.renderOutline(x, y, width, (int) (width * 0.1f) + 10, 0xFF909090);
    }

    public static <T extends Tag> void drawProgressBar(GuiGraphics graphics, int minX, int minY, int maxX, int maxY, QuestTask<?, T, ?> task, TaskProgress<T> progress) {
        graphics.fill(minX, minY, maxX, maxY, 0xFF808080);
        graphics.fill(minX + 1, minY + 1, maxX - 1, maxY - 1, 0xFF696969);
        float fill = task.getProgress(progress.progress());
        int progressWidth = (int) (((maxX - 1) - (minX + 1)) * fill);
        graphics.fill(minX + 1, minY + 1, minX + 1 + progressWidth, maxY - 1, progress.isComplete() ? 0xFF04CB40 : 0xFF5691FF);
    }

    public static <T extends Tag> void drawProgressText(GuiGraphics graphics, int x, int y, int width, QuestTask<?, T, ?> task, TaskProgress<T> progress) {
        Font font = Minecraft.getInstance().font;
        String text = QuestTaskDisplayFormatter.create(task, progress);
        graphics.drawString(
            font,
            text, x + width - 5 - font.width(text), y + 5, 0xFFFFFFFF,
            false
        );
    }
}
