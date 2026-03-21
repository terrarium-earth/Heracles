package earth.terrarium.olympus.client.pipelines;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import earth.terrarium.olympus.client.pipelines.pips.RoundedRectanglePIPRenderer;
import earth.terrarium.olympus.client.pipelines.uniforms.RoundedRectangleUniform;
import earth.terrarium.olympus.client.utils.GuiGraphicsHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class RoundedRectangle {

    public static final RenderPipeline PIPELINE = RenderPipeline.builder()
            .withLocation(Identifier.fromNamespaceAndPath("olympus", "rounded_rect"))
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform(RoundedRectangleUniform.NAME, UniformType.UNIFORM_BUFFER)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withFragmentShader(Identifier.fromNamespaceAndPath("olympus", "core/rounded_rect"))
            .withVertexShader(Identifier.fromNamespaceAndPath("olympus", "core/rounded_rect"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build();

    public static void draw(
            GuiGraphics graphics,
            int x, int y, int width, int height,
            int backgroundColor, int borderColor,
            float borderRadius, int borderWidth
    ) {
        GuiGraphicsHelper.submitPip(graphics, new RoundedRectanglePIPRenderer.State(
                graphics,
                x, y, width, height,
                backgroundColor, borderColor, (int) borderRadius, borderWidth
        ));
    }

    public static void draw(
            GuiGraphics graphics,
            int x, int y, int width, int height,
            int backgroundColor,
            int borderColorTopLeft, int borderColorTopRight,
            int borderColorBottomLeft, int borderColorBottomRight,
            int borderRadius, int borderWidth
    ) {
        GuiGraphicsHelper.submitPip(graphics, new RoundedRectanglePIPRenderer.State(
                graphics,
                x, y, width, height,
                backgroundColor,
                borderColorTopLeft, borderColorTopRight, borderColorBottomLeft, borderColorBottomRight,
                borderRadius, borderWidth
        ));
    }
}
