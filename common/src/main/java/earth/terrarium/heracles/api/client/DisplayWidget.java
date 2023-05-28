package earth.terrarium.heracles.api.client;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import net.minecraft.client.gui.GuiGraphics;

public interface DisplayWidget {

    void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks);

    int getHeight(int width);

    default boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        return false;
    }
}
