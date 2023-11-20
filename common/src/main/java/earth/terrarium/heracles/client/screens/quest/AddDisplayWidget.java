package earth.terrarium.heracles.client.screens.quest;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.DisplayWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record AddDisplayWidget(Runnable onClicked) implements DisplayWidget {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/widgets.png");

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        y += 10;
        hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 30;

        RenderSystem.enableBlend();
        graphics.blitNineSliced(TEXTURE, x, y, width, 30, 3, 128, 30, 128, hovered ? 196 : 226);
        graphics.blit(TEXTURE, x + (width / 2) - 16, y, 96, hovered ? 196 : 226, 30, 30);
        RenderSystem.disableBlend();
        CursorUtils.setCursor(hovered, CursorScreen.Cursor.POINTER);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        if (mouseButton != 0) return false;
        if (mouseX < 0 || mouseX > width) return false;
        if (mouseY < 10 || mouseY > 40) return false;
        onClicked.run();
        return true;
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}
