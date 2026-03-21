package earth.terrarium.olympus.client.utils;

import com.teamresourceful.resourcefullib.common.exceptions.NotImplementedException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GuiGraphicsHelper {

    @ExpectPlatform
    public static void submitPip(GuiGraphics graphics, PictureInPictureRenderState state) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static void submitElement(GuiGraphics graphics, GuiElementRenderState state) {
        throw new NotImplementedException();
    }

    @ExpectPlatform
    public static ScreenRectangle getLastScissor(GuiGraphics graphics) {
        throw new NotImplementedException();
    }
}
