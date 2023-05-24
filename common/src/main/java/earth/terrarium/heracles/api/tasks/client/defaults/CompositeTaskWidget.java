package earth.terrarium.heracles.api.tasks.client.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatters;
import earth.terrarium.heracles.api.tasks.defaults.CompositeTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class CompositeTaskWidget implements DisplayWidget {

    private final CompositeTask task;
    private final TaskProgress<CollectionTag<Tag>> progress;
    private final List<DisplayWidget> widgets;

    private boolean isOpened = false;

    public CompositeTaskWidget(CompositeTask task, TaskProgress<CollectionTag<Tag>> progress) {
        this.task = task;
        this.progress = progress;
        this.widgets = new ArrayList<>();
        int i = 0;
        for (QuestTask<?, ?, ?> value : task.tasks().values()) {
            TaskProgress<?> valueProgress = new TaskProgress<>(progress.progress().get(i), false);
            valueProgress.updateComplete(ModUtils.cast(value));
            this.widgets.add(QuestTaskWidgets.create(ModUtils.cast(value), valueProgress));
            i++;
        }
    }

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        int height = getHeight(width);
        Gui.fill(pose, x, y, x + width, y + height, 0x80808080);
        Gui.renderOutline(pose, x, y, width, height, 0xFF909090);

        Font font = Minecraft.getInstance().font;
        int start = font.draw(pose, isOpened ? "▼" : "▶", x + (int) (width * 0.1f) + 10, y + 5, 0xFFFFFFFF);
        font.draw(pose, Component.translatable(TaskTitleFormatters.toTranslationKey(this.task, this.task.amount() == 1), this.task.amount()), start + 2, y + 5, 0xFFFFFFFF);
        String progress = QuestTaskDisplayFormatter.create(this.task, this.progress);
        font.draw(pose, progress, x + width - 5 - font.width(progress), y + 5, 0xFFFFFFFF);

        if (isOpened) {
            int yOffset = 19;
            for (DisplayWidget widget : this.widgets) {
                widget.render(pose, scissor, x + 10, y + yOffset, width - 20, mouseX, mouseY, hovered, partialTicks);
                yOffset += widget.getHeight(width - 20);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        if (mouseY < 0 || mouseY > getHeight(width)) return false;
        if (mouseX < 0 || mouseX > width) return false;
        if (mouseButton != 0) return false;
        if (mouseY < 19) {
            this.isOpened = !this.isOpened;
            return true;
        }
        return false;
    }

    @Override
    public int getHeight(int width) {
        if (isOpened) {
            int size = 29;
            for (DisplayWidget widget : this.widgets) {
                size += widget.getHeight(width - 20);
            }
            return size;
        }
        return 19;
    }

}
