package earth.terrarium.heracles.client.screens.quest.rewards;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.utils.ThemeColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public record QuestDependentWidget(Quest quest) implements DisplayWidget {

    private static final String TITLE = "gui.heracles.rewards.unlocks_quest.title";
    private static final Component DESCRIPTION = Component.translatable("gui.heracles.rewards.unlocks_quest.desc");

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        quest.display().icon().render(graphics, scissor, x + 5, y + 5, iconSize, iconSize);
        graphics.drawString(
            font,
            Component.translatable(TITLE, this.quest.display().title()), x + iconSize + 16, y + 6, ThemeColors.REWARD_TITLE,
            false
        );
        graphics.drawString(
            font,
            DESCRIPTION, x + iconSize + 16, y + 8 + font.lineHeight, ThemeColors.REWARD_DESCRIPTION,
            false
        );
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}
