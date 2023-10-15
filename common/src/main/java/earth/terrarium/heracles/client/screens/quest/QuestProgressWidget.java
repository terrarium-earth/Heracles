package earth.terrarium.heracles.client.screens.quest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

public record QuestProgressWidget(int x, int y, int width, int tasks, int completed, int rewards, int claimed) implements Renderable {

    private static final String TITLE_INCOMPLETE =  "gui.heracles.progress.title.incomplete";
    private static final String TITLE_COMPLETE =  "gui.heracles.progress.title.complete";
    private static final String DESC_SINGULAR = "gui.heracles.progress.desc.incomplete.singular";
    private static final String DESC_PLURAL = "gui.heracles.progress.desc.incomplete.plural";
    private static final String DESC_COMPLETE_SINGULAR = "gui.heracles.progress.desc.complete.singular";
    private static final String DESC_COMPLETE_PLURAL = "gui.heracles.progress.desc.complete.plural";
    private static final String DESC_COMPLETE_CLAIMED = "gui.heracles.progress.desc.complete_claimed";

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(x, y, x + width, y + 30, 0xD0000000);
        graphics.renderOutline(x, y, width, 30, tasks == completed ? (rewards == claimed ? 0xFF32C143 : 0xFFC7C700) : 0xFFFFFFFF);

        String title = tasks == completed ? TITLE_COMPLETE : TITLE_INCOMPLETE;
        String desc = tasks == completed ? (rewards == claimed ? DESC_COMPLETE_CLAIMED : (rewards - claimed > 1 ? DESC_COMPLETE_PLURAL : DESC_COMPLETE_SINGULAR)) : (tasks - completed > 1 ? DESC_PLURAL : DESC_SINGULAR);
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
            Component.translatable(desc, tasks == completed ? rewards - claimed : tasks - completed), x + 5, y + 25 - Minecraft.getInstance().font.lineHeight, 0xFF696969,
            false
        );
    }
}
