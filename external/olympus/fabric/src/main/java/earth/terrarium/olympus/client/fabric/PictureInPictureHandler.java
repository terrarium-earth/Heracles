package earth.terrarium.olympus.client.fabric;

import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public class PictureInPictureHandler implements Closeable {

    private final Map<Class<? extends PictureInPictureRenderState>, PictureInPicturePool<?>> pool = new HashMap<>();
    private final MultiBufferSource.BufferSource source;

    public PictureInPictureHandler(MultiBufferSource.BufferSource source) {
        this.source = source;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends OlympusPictureInPictureRenderState<T>> PictureInPicturePool<PictureInPictureRenderState> getPool(OlympusPictureInPictureRenderState<T> state) {
        return (PictureInPicturePool<PictureInPictureRenderState>) pool.computeIfAbsent(
                state.getClass(),
                it -> {
                    var factory = state.getFactory();
                    return factory == null ? null : new PictureInPicturePool<>(() -> factory.apply(source));
                }
        );
    }

    public void end() {
        pool.values().forEach(PictureInPicturePool::end);
    }

    @Override
    public void close() {
        pool.values().forEach(PictureInPicturePool::close);
        pool.clear();
    }
}
