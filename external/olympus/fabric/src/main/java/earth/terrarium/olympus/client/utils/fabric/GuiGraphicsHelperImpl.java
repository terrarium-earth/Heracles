package earth.terrarium.olympus.client.utils.fabric;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

public class GuiGraphicsHelperImpl {
    public static void submitPip(GuiGraphics graphics, PictureInPictureRenderState state) {
        graphics.guiRenderState.submitPicturesInPictureState(state);
    }

    public static void submitElement(GuiGraphics graphics, GuiElementRenderState state) {
        graphics.guiRenderState.submitGuiElement(state);
    }

    public static ScreenRectangle getLastScissor(GuiGraphics graphics) {
        return graphics.scissorStack.peek();
    }
}
