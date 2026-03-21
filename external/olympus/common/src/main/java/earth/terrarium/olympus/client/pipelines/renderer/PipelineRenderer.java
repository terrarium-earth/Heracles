package earth.terrarium.olympus.client.pipelines.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;

public class PipelineRenderer {

    private record Buffers(
            GpuBuffer vertex,
            GpuBuffer index,
            VertexFormat.IndexType type
    ){
        private static Buffers of(MeshData mesh, RenderPipeline pipeline) {
            GpuBuffer vertex = pipeline.getVertexFormat().uploadImmediateVertexBuffer(mesh.vertexBuffer());
            if (mesh.indexBuffer() == null) {
                var storage = RenderSystem.getSequentialBuffer(mesh.drawState().mode());
                return new Buffers(
                        vertex,
                        storage.getBuffer(mesh.drawState().indexCount()),
                        storage.type()
                );
            }
            return new Buffers(
                    vertex,
                    pipeline.getVertexFormat().uploadImmediateIndexBuffer(mesh.indexBuffer()),
                    mesh.drawState().indexType()
            );
        }
    }

    private static GpuBufferSlice getDynamicUniforms(int color) {
        return RenderSystem.getDynamicUniforms()
                .writeTransform(
                        RenderSystem.getModelViewMatrix(),
                        new Vector4f(ARGB.redFloat(color), ARGB.greenFloat(color), ARGB.blueFloat(color), ARGB.alphaFloat(color)),
                        new Vector3f(),
                        new Matrix4f()
                );
    }

    protected static void draw(
            RenderPipeline pipeline,
            MeshData mesh,
            int color,
            TextureSetup textures,
            Consumer<RenderPass> options
    ) {
        GpuDevice device = RenderSystem.getDevice();

        var buffers = Buffers.of(mesh, pipeline);

        var target = Minecraft.getInstance().getMainRenderTarget();
        var uniforms = getDynamicUniforms(color);

        try (mesh; var pass = device.createCommandEncoder().createRenderPass(
                () -> "Olympus Pipeline Render Pass for: " + pipeline.getLocation(),
                Objects.requireNonNullElse(RenderSystem.outputColorTextureOverride, target.getColorTextureView()),
                OptionalInt.empty(),
                target.useDepth ? Objects.requireNonNullElse(RenderSystem.outputDepthTextureOverride, target.getDepthTextureView()) : null,
                OptionalDouble.empty()
        )) {
            pass.setPipeline(pipeline);

            var scissor = RenderSystem.getScissorStateForRenderTypeDraws();
            if (scissor.enabled()) {
                pass.enableScissor(scissor.x(), scissor.y(), scissor.width(), scissor.height());
            }

            if (textures.texure0() != null) pass.bindTexture("Sampler0", textures.texure0(), textures.sampler0());
            if (textures.texure1() != null) pass.bindTexture("Sampler1", textures.texure1(), textures.sampler1());
            if (textures.texure2() != null) pass.bindTexture("Sampler2", textures.texure2(), textures.sampler2());

            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", uniforms);

            options.accept(pass);

            pass.setVertexBuffer(0, buffers.vertex());
            pass.setIndexBuffer(buffers.index(), buffers.type());

            pass.drawIndexed(0, 0, mesh.drawState().indexCount(), 1);
        }
    }

    public static PipelineRendererBuilder builder(RenderPipeline pipeline, MeshData mesh) {
        return new PipelineRendererBuilder(pipeline, mesh);
    }
}
