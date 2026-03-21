package earth.terrarium.olympus.client.pipelines.pips;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import earth.terrarium.olympus.client.pipelines.RoundedTexture;
import earth.terrarium.olympus.client.pipelines.renderer.PipelineRenderer;
import earth.terrarium.olympus.client.pipelines.uniforms.RoundedTextureUniform;
import earth.terrarium.olympus.client.utils.GuiGraphicsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.function.Function;

public class RoundedTexturePIPRenderer extends PictureInPictureRenderer<RoundedTexturePIPRenderer.@NotNull State> {

    private State lastState;

    public RoundedTexturePIPRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public @NotNull Class<State> getRenderStateClass() {
        return RoundedTexturePIPRenderer.State.class;
    }

    @Override
    protected boolean textureIsReadyToBlit(State state) {
        return this.lastState != null && this.lastState.equals(state);
    }

    @Override
    protected void renderToTexture(State state, @NotNull PoseStack stack) {
        var bounds = state.bounds;

        float scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
        float scaledWidth = bounds.width() * scale;
        float scaledHeight = bounds.height() * scale;

        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(0f, 0f, 0f).setUv(state.u0(), state.v0()).setColor(-1);
        buffer.addVertex(0f, scaledHeight, 0f).setUv(state.u0(), state.v1()).setColor(-1);
        buffer.addVertex(scaledWidth, scaledHeight, 0f).setUv(state.u1(), state.v1()).setColor(-1);
        buffer.addVertex(scaledWidth, 0f, 0f).setUv(state.u1(), state.v0()).setColor(-1);

        PipelineRenderer.builder(RoundedTexture.PIPELINE, buffer.buildOrThrow())
                .uniform(RoundedTextureUniform.STORAGE, RoundedTextureUniform.of(
                        new Vector4f(state.borderRadius()),
                        new Vector2f(scaledWidth, scaledHeight),
                        new Vector2f(scaledWidth / 2f, scaledHeight / 2f),
                        scale
                ))
                .textures(state.texture())
                .color(state.color())
                .draw();

        this.lastState = state;
    }

    @Override
    protected @NotNull String getTextureLabel() {
        return "olympus_rounded_texture";
    }

    public record State(
            int x0, int y0, int x1, int y1,
            float u0, float v0, float u1, float v1,
            TextureSetup texture, int color,
            int borderRadius,
            Matrix3x2f pose, ScreenRectangle scissorArea, ScreenRectangle bounds
    ) implements OlympusPictureInPictureRenderState<State> {

        public State(
                GuiGraphics graphics,
                int x, int y, int width, int height,
                float u0, float v0, float u1, float v1,
                TextureSetup texture, int color, int borderRadius
        ) {
            this(
                    x, y, x + width, y + height,
                    u0, v0, u1, v1,
                    texture, color, borderRadius,
                    new Matrix3x2f(graphics.pose()), GuiGraphicsHelper.getLastScissor(graphics),
                    OlympusPictureInPictureRenderState.getRelativeBounds(graphics, x, y, x + width, y + height)
            );
        }

        @Override
        public float scale() {
            return 1f;
        }

        @Override
        public Function<MultiBufferSource.BufferSource, PictureInPictureRenderer<State>> getFactory() {
            return RoundedTexturePIPRenderer::new;
        }
    }
}
