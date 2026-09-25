package earth.terrarium.heracles.client.components.lists.tasks.entries;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.client.components.lists.BaseListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public record DependencyTaskEntry(Quest quest) implements BaseListEntry<QuestTask<?, ?, ?>> {

    private static final int ICON_SIZE = 32;

    private static final Component TITLE = Component.translatable("task.heracles.require_quest.title");
    private static final String DESCRIPTION = "task.heracles.require_quest.desc";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        BaseListEntry.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);

        Font font = Minecraft.getInstance().font;
        quest.display().icon().render(graphics, x + 5, y + 5, ICON_SIZE, ICON_SIZE);
        graphics.drawString(
            font,
            TITLE, x + ICON_SIZE + 16, y + 6, QuestScreenTheme.getRewardTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESCRIPTION, this.quest.display().title()), x + ICON_SIZE + 16, y + 8 + font.lineHeight, QuestScreenTheme.getRewardDescription(),
            false
        );
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }

    @Override
    public QuestTask<?, ?, ?> value() {
        return null;
    }
}
