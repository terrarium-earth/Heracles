package earth.terrarium.heracles.client.screens.quest.tasks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;

public record TaskListHeadingWidget(float completion) implements DisplayWidget {

    private static final Component TITLE = Component.translatable("gui.heracles.tasks.title");
    private static final Component DESCRIPTION = Component.translatable("gui.heracles.tasks.description");

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Gui.fill(pose, x, y, x + width, y + 30, 0xD0000000);
        Gui.renderOutline(pose, x, y, width, 30, 0xFFFFFFFF);
        Minecraft.getInstance().font.draw(pose, TITLE, x + 5, y + 5, 0xFFFFFFFF);
        Minecraft.getInstance().font.draw(pose, String.format("%.2f%%", this.completion * 100), x + width - 5 - Minecraft.getInstance().font.width(String.format("%.2f%%", this.completion * 100)), y + 5, 0xFFFFFFFF);
        Minecraft.getInstance().font.draw(pose, DESCRIPTION, x + 5, y + 25 - Minecraft.getInstance().font.lineHeight, 0xFF696969);
    }

    @Override
    public int getHeight(int width) {
        return 30;
    }
}
