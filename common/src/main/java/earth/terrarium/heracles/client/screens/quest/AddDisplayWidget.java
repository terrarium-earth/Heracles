package earth.terrarium.heracles.client.screens.quest;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import net.minecraft.client.gui.GuiGraphics;

public record AddDisplayWidget(Runnable onClicked) implements DisplayWidget {

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        y += 10;
        hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 30;
        int hoveredColor = hovered ? 0xFFFFFFFF : 0xFFA0A0A0;
        graphics.fill(x, y, x + width, y + 30, 0xD0000000);
        graphics.renderOutline(x, y, width, 30, hoveredColor);

        int plusX = x + ((width - 20) / 2);

        graphics.fill(plusX, y + 13, plusX + 20, y + 17, hoveredColor);
        graphics.fill(plusX + 8, y + 5, plusX + 12, y + 25, hoveredColor);
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
        return 40;
    }
}
