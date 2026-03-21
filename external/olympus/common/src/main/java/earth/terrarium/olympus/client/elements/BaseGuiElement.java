package earth.terrarium.olympus.client.elements;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import earth.terrarium.olympus.client.utils.GuiGraphicsHelper;
import earth.terrarium.olympus.client.utils.TextureUtils;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public abstract class BaseGuiElement implements GuiElementRenderState {

    private final RenderPipeline pipeline;
    private final TextureSetup texture;

    private Matrix3x2f pose;
    private ScreenRectangle scissor;
    private ScreenRectangle bounds;

    public BaseGuiElement(RenderPipeline pipeline, TextureSetup texture) {
        this.pipeline = pipeline;
        this.texture = texture;
    }

    public BaseGuiElement(RenderPipeline pipeline) {
        this(pipeline, TextureSetup.noTexture());
    }

    public BaseGuiElement(RenderPipeline pipeline, Identifier texture) {
        this(pipeline, TextureUtils.single(texture));
    }

    @Override
    public @NotNull RenderPipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public @NotNull TextureSetup textureSetup() {
        return this.texture;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return this.scissor;
    }

    @Override
    public @NotNull ScreenRectangle bounds() {
        return this.bounds;
    }

    public @NotNull Matrix3x2f pose() {
        return this.pose;
    }

    @Override
    public abstract void buildVertices(@NotNull VertexConsumer consumer);

    public void submit(@NotNull GuiGraphics graphics, int x, int y, int width, int height) {
        this.pose = new Matrix3x2f(graphics.pose());
        this.scissor = GuiGraphicsHelper.getLastScissor(graphics);
        this.bounds = Optionull.mapOrElse(
                this.scissor,
                rect -> rect.intersection(new ScreenRectangle(x, y, width, height).transformMaxBounds(this.pose)),
                () -> new ScreenRectangle(x, y, width, height).transformMaxBounds(this.pose)
        );

        GuiGraphicsHelper.submitElement(graphics, this);
    }
}
