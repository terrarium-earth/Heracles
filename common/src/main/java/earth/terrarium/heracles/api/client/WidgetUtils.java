package earth.terrarium.heracles.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.gui.Gui;

public final class WidgetUtils {

    public static void drawBackground(PoseStack pose, int x, int y, int width) {
        Gui.fill(pose, x, y, x + width, y + (int) (width * 0.1f) + 10, 0x80808080);
        Gui.renderOutline(pose, x, y, width, (int) (width * 0.1f) + 10, 0xFF909090);
    }

    public static void drawProgressBar(PoseStack pose, int minX, int minY, int maxX, int maxY, TaskProgress progress, int target) {
        Gui.fill(pose, minX, minY, maxX, maxY, 0xFF808080);
        Gui.fill(pose, minX + 1, minY + 1, maxX - 1, maxY - 1, 0xFF696969);
        float fill = (float) progress.progress() / (float) target;
        int progressWidth = (int) (((maxX - 1) - (minX + 1)) * fill);
        Gui.fill(pose, minX + 1, minY + 1, minX + 1 + progressWidth, maxY - 1, progress.isComplete() ? 0xFF04CB40 : 0xFF5691FF);
    }
}
