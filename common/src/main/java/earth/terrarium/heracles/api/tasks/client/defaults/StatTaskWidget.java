package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.StatTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public record StatTaskWidget(
    StatTask task, TaskProgress<NumericTag> progress
) implements DisplayWidget {

    private static final String DESCRIPTION = "task.heracles.stat.desc.singular";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        if (this.task.icon().isVisible()) {
            this.task.icon().render(graphics, scissor, x, y, iconSize, iconSize);
        } else {
            WidgetUtils.drawItemIcon(graphics, Items.SPYGLASS.getDefaultInstance(), x, y, iconSize);
        }
        graphics.fill(x + iconSize + 9, y + 5, x + iconSize + 10, y + getHeight(width) - 5, 0xFF909090);
        graphics.drawString(
            font,
            !task.title().isEmpty() ? Component.translatable(task.title()) : TaskTitleFormatter.create(this.task), x + iconSize + 16, y + 6, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESCRIPTION, Component.translatable(task.stat().toLanguageKey("stat")), task.target()), x + iconSize + 16, y + 8 + font.lineHeight, 0xFF808080,
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
