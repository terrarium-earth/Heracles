package earth.terrarium.olympus.client.pipelines.renderer;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.datafixers.util.Pair;
import earth.terrarium.olympus.client.pipelines.uniforms.RenderPipelineUniforms;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.DynamicUniformStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PipelineRendererBuilder {

    private final RenderPipeline pipeline;
    private final MeshData mesh;

    private final List<UniformEntry<?>> uniforms = new ArrayList<>();
    private TextureSetup textures = TextureSetup.noTexture();
    private int color = -1;

    protected PipelineRendererBuilder(RenderPipeline pipeline, MeshData mesh) {
        this.pipeline = pipeline;
        this.mesh = mesh;
    }

    public <T extends RenderPipelineUniforms> PipelineRendererBuilder uniform(Supplier<DynamicUniformStorage<T>> storage, T uniform) {
        this.uniforms.add(new UniformEntry<>(uniform, storage));
        return this;
    }

    public PipelineRendererBuilder textures(TextureSetup textures) {
        this.textures = textures;
        return this;
    }

    public PipelineRendererBuilder color(int color) {
        this.color = color;
        return this;
    }

    public void draw() {
        List<Pair<String, GpuBufferSlice>> dynamicUniforms = new ArrayList<>();
        for (UniformEntry<?> entry : this.uniforms) {
            dynamicUniforms.add(Pair.of(entry.uniform.name(), entry.write()));
        }
        PipelineRenderer.draw(this.pipeline, this.mesh, this.color, this.textures, pass -> {
            for (var entry : dynamicUniforms) {
                pass.setUniform(entry.getFirst(), entry.getSecond());
            }
        });
    }

    private record UniformEntry<T extends RenderPipelineUniforms>(
            T uniform,
            Supplier<DynamicUniformStorage<T>> storage
    ) {

        public GpuBufferSlice write() {
            return storage.get().writeUniform(uniform);
        }
    }
}
