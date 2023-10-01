package earth.terrarium.heracles.client.screens.quest.tasks;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.quests.Quest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public record DependencyDisplayWidget(Quest quest) implements DisplayWidget {

    private static final Component TITLE = Component.translatable("task.heracles.require_quest.title");
    private static final String DESCRIPTION = "task.heracles.require_quest.desc";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width);
        int iconSize = (int) (width * 0.1f);
        quest.display().icon().render(graphics, scissor, x + 5, y + 5, iconSize, iconSize);
        graphics.drawString(
            font,
            TITLE, x + iconSize + 10, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESCRIPTION, this.quest.display().title()), x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080,
            false
        );
    }

    @Override
    public int getHeight(int width) {
        return (int) (width * 0.1f) + 10;
    }
}
