package earth.terrarium.olympus.client.pipelines;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import earth.terrarium.olympus.client.pipelines.pips.RoundedTexturePIPRenderer;
import earth.terrarium.olympus.client.pipelines.uniforms.RoundedTextureUniform;
import earth.terrarium.olympus.client.utils.GuiGraphicsHelper;
import earth.terrarium.olympus.client.utils.TextureUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class RoundedTexture {

    public static final RenderPipeline PIPELINE = RenderPipeline.builder()
            .withLocation(Identifier.fromNamespaceAndPath("olympus", "rounded_tex"))
            .withSampler("Sampler0")
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform(RoundedTextureUniform.NAME, UniformType.UNIFORM_BUFFER)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withFragmentShader(Identifier.fromNamespaceAndPath("olympus", "core/rounded_tex"))
            .withVertexShader(Identifier.fromNamespaceAndPath("olympus", "core/rounded_tex"))
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
            .build();

    public static void draw(
            GuiGraphics graphics,
            int x, int y, int width, int height,
            Identifier texture,
            float u0, float v0, float u1, float v1,
            float radius
    ) {
        draw(graphics, x, y, width, height, texture, u0, v0, u1, v1, radius, 0xFFFFFFFF);
    }

    public static void draw(
            GuiGraphics graphics,
            int x, int y, int width, int height,
            Identifier texture,
            float u0, float v0, float u1, float v1,
            float radius, int color
    ) {
        GuiGraphicsHelper.submitPip(graphics, new RoundedTexturePIPRenderer.State(
                graphics,
                x, y, width,height,
                u0, v0, u1, v1,
                TextureUtils.single(texture), color, (int) radius
        ));
    }
}
