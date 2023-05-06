package earth.terrarium.heracles.client.screens.quest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;

public record HeadingWidget(Component title, int color) implements DisplayWidget {

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        y += 5;
        int titleWidth = Minecraft.getInstance().font.width(title);
        Gui.fill(pose, x, y, x + titleWidth + 6, y + Minecraft.getInstance().font.lineHeight + 4, color);
        Gui.fill(pose, x + titleWidth + 6, y + Minecraft.getInstance().font.lineHeight, x + width, y + Minecraft.getInstance().font.lineHeight + 4, color);
        //Draw Border
        Gui.fill(pose, x, y, x + 1, y + Minecraft.getInstance().font.lineHeight + 4, 0xFF909090);
        Gui.fill(pose, x + 1, y, x + titleWidth + 6, y + 1, 0xFF909090);
        Gui.fill(pose, x + titleWidth + 5, y, x + titleWidth + 6, y + Minecraft.getInstance().font.lineHeight + 1, 0xFF909090);
        Gui.fill(pose, x + titleWidth + 6, y + Minecraft.getInstance().font.lineHeight, x + width, y + Minecraft.getInstance().font.lineHeight + 1, 0xFF909090);
        Gui.fill(pose, x + width - 1, y + Minecraft.getInstance().font.lineHeight, x + width, y + Minecraft.getInstance().font.lineHeight + 4, 0xFF909090);


        Minecraft.getInstance().font.draw(pose, title, x + 3, y + 3, 0xFFFFFFFF);
    }

    @Override
    public int getHeight(int width) {
        return 5 + Minecraft.getInstance().font.lineHeight + 4;
    }
}
