package earth.terrarium.heracles.client.screens.quest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

public final class QuestProgressWidget implements Renderable {
    private static final String TITLE_INCOMPLETE = "gui.heracles.progress.title.incomplete";
    private static final String TITLE_COMPLETE = "gui.heracles.progress.title.complete";
    private static final String DESC_SINGULAR = "gui.heracles.progress.desc.incomplete.singular";
    private static final String DESC_PLURAL = "gui.heracles.progress.desc.incomplete.plural";
    private static final String DESC_COMPLETE = "gui.heracles.progress.desc.complete";
    private final int x;
    private final int y;
    private final int width;
    private int tasks = 0;
    private int completed = 0;

    public QuestProgressWidget(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public void update(int tasks, int completed) {
        this.tasks = tasks;
        this.completed = completed;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + 30, 0xD0000000);
        graphics.renderOutline(x, y, width, 30, 0xFFFFFFFF);

        String title = tasks == completed ? TITLE_COMPLETE : TITLE_INCOMPLETE;
        String desc = tasks == completed ? DESC_COMPLETE : (tasks - completed > 1 ? DESC_PLURAL : DESC_SINGULAR);
        String completion = String.format("%.0f%%", this.completed * 100 / (double) tasks);

        graphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable(title), x + 5, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            Minecraft.getInstance().font,
            completion, x + width - 5 - Minecraft.getInstance().font.width(completion), y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable(desc, tasks - completed), x + 5, y + 25 - Minecraft.getInstance().font.lineHeight, 0xFF696969,
            false
        );
    }
}
