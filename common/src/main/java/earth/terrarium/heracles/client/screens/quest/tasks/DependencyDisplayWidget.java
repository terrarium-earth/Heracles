package earth.terrarium.heracles.client.screens.quest.tasks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.quests.Quest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public record DependencyDisplayWidget(Quest quest) implements DisplayWidget {

    private static final String TITLE = "gui.heracles.task.require_quest.title";
    private static final Component DESCRIPTION = Component.translatable("gui.heracles.task.require_quest.desc");

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(pose, x, y, width);
        int iconSize = (int) (width * 0.1f);
        quest.display().icon().render(pose, scissor, x + 5, y + 5, iconSize, iconSize);
        font.draw(pose, Component.translatable(TITLE, this.quest.display().title()), x + iconSize + 10, y + 5, 0xFFFFFFFF);
        font.draw(pose, DESCRIPTION, x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080);
    }

    @Override
    public int getHeight(int width) {
        return (int) (width * 0.1f) + 10;
    }
}
