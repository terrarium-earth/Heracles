package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.DummyTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.CommonComponents;

public record DummyTaskWidget(
    DummyTask task, TaskProgress<ByteTag> progress
) implements DisplayWidget {

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        WidgetUtils.drawItemIcon(graphics, task.icon().getDefaultInstance(), x, y, iconSize);
        graphics.fill(x + iconSize + 9, y + 5, x + iconSize + 10, y + getHeight(width) - 5, 0xFF909090);
        graphics.drawString(
            font,
            TaskTitleFormatter.create(this.task), x + iconSize + 16, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            this.task.description() == null ? CommonComponents.EMPTY : this.task.description(), x + iconSize + 16, y + 7 + font.lineHeight, 0xFF808080,
            false
        );
        WidgetUtils.drawProgressText(graphics, x, y, width, this.task, this.progress);
        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 16, y + height - font.lineHeight - 6, x + width - 5, y + height - 5, this.task, this.progress);
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}
