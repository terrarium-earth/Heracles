package earth.terrarium.olympus.client.pipelines.pips;

import earth.terrarium.olympus.client.utils.GuiGraphicsHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface OlympusPictureInPictureRenderState<T extends OlympusPictureInPictureRenderState<T>> extends PictureInPictureRenderState {

    Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<T>> getFactory();

    @Nullable
    static ScreenRectangle getRelativeBounds(GuiGraphics graphics, int x0, int y0, int x1, int y1) {
        var scissor = GuiGraphicsHelper.getLastScissor(graphics);
        var pose = graphics.pose();
        var bounds = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
        return scissor != null ? scissor.intersection(bounds) : bounds;
    }
}
