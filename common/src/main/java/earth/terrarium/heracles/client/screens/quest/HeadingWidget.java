package earth.terrarium.heracles.client.screens.quest;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public record HeadingWidget(Component title, ModUtils.QuestStatus status) implements DisplayWidget {

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        y += 5;
        int titleWidth = Minecraft.getInstance().font.width(title);

        RenderSystem.enableBlend();
        switch (status) {
            case LOCKED -> {
                graphics.blitSprite(Heracles.id("widgets/heading_status_locked"), x, y, titleWidth + 6, Minecraft.getInstance().font.lineHeight + 4);
                graphics.blitSprite(Heracles.id("widgets/heading_status_locked_other"), x + titleWidth + 6, y, width - titleWidth - 6, Minecraft.getInstance().font.lineHeight + 4);
            }
            case IN_PROGRESS -> {
                graphics.blitSprite(Heracles.id("widgets/heading_status_in_progress"), x, y, titleWidth + 6, Minecraft.getInstance().font.lineHeight + 4);
                graphics.blitSprite(Heracles.id("widgets/heading_status_in_progress_other"), x + titleWidth + 6, y, width - titleWidth - 6, Minecraft.getInstance().font.lineHeight + 4);
            }
            case COMPLETED -> {
                graphics.blitSprite(Heracles.id("widgets/heading_status_completed"), x, y, titleWidth + 6, Minecraft.getInstance().font.lineHeight + 4);
                graphics.blitSprite(Heracles.id("widgets/heading_status_completed_other"), x + titleWidth + 6, y, width - titleWidth - 6, Minecraft.getInstance().font.lineHeight + 4);
            }
            case COMPLETED_CLAIMED -> {
                graphics.blitSprite(Heracles.id("widgets/heading_status_completed_claimed"), x, y, titleWidth + 6, Minecraft.getInstance().font.lineHeight + 4);
                graphics.blitSprite(Heracles.id("widgets/heading_status_completed_claimed_other"), x + titleWidth + 6, y, width - titleWidth - 6, Minecraft.getInstance().font.lineHeight + 4);
            }
        }
        RenderSystem.disableBlend();

        graphics.drawString(
            Minecraft.getInstance().font,
            title, x + 3, y + 3, QuestScreenTheme.getTaskRewardStatusHeading(),
            false
        );
    }

    @Override
    public int getHeight(int width) {
        return 5 + Minecraft.getInstance().font.lineHeight + 4;
    }
}
