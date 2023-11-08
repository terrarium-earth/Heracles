package earth.terrarium.heracles.client.screens.quest;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record HeadingWidget(Component title, ModUtils.QuestStatus status) implements DisplayWidget {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/widgets.png");

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        y += 5;
        int titleWidth = Minecraft.getInstance().font.width(title);

        RenderSystem.enableBlend();
        graphics.blitNineSliced(TEXTURE, x, y, titleWidth + 6, Minecraft.getInstance().font.lineHeight + 4, 3, 64, 13, 0, 42 + status.ordinal() * 13);
        graphics.blitNineSliced(TEXTURE, x + titleWidth + 6, y, width - titleWidth - 6, Minecraft.getInstance().font.lineHeight + 4, 4, 64, 13, 64, 42 + status.ordinal() * 13);
        RenderSystem.disableBlend();

        graphics.drawString(
            Minecraft.getInstance().font,
            title, x + 3, y + 3, 0xFFFFFFFF,
            false
        );
    }

    @Override
    public int getHeight(int width) {
        return 5 + Minecraft.getInstance().font.lineHeight + 4;
    }
}
