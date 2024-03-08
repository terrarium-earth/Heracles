package earth.terrarium.heracles.client.components.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record HeadingListEntry<T>(Component title, ResourceLocation texture) implements ListEntry<T> {

    private static final int OFFSET = 5;
    private static final int TEXT_PADDING = 3;
    private static final int TEXT_X_PADDING = TEXT_PADDING * 2;
    private static final int HEADING_HEIGHT = 13;
    private static final int TEXT_BANNER_WIDTH = 64;

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {

        Font font = Minecraft.getInstance().font;

        int titleWidth = font.width(title);

        RenderSystem.enableBlend();
        graphics.blitNineSliced(texture(),
            x, y + OFFSET,
            titleWidth + TEXT_X_PADDING, HEADING_HEIGHT,
            3, TEXT_BANNER_WIDTH, HEADING_HEIGHT,
            0, 0
        );
        graphics.blitNineSliced(texture(),
            x + titleWidth + TEXT_X_PADDING, y + OFFSET,
            width - titleWidth - TEXT_X_PADDING, HEADING_HEIGHT,
            4, TEXT_BANNER_WIDTH, HEADING_HEIGHT,
            TEXT_BANNER_WIDTH, 0
        );
        RenderSystem.disableBlend();

        graphics.drawString(
            font,
            title,
            x + TEXT_PADDING, y + OFFSET + TEXT_PADDING,
            QuestScreenTheme.getTaskRewardStatusHeading(), false
        );
    }

    @Override
    public int getHeight(int width) {
        return OFFSET + HEADING_HEIGHT;
    }

    @Override
    public T value() {
        return null;
    }
}