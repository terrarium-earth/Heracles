package earth.terrarium.olympus.client.fabric;

import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PictureInPicturePool<T extends PictureInPictureRenderState> implements Closeable {

    private final Map<T, PoolEntry<T>> pool = new HashMap<>();
    private final Supplier<PictureInPictureRenderer<T>> factory;

    public PictureInPicturePool(Supplier<PictureInPictureRenderer<T>> factory) {
        this.factory = factory;
    }

    public void prepare(T state, GuiRenderState gui, int scale) {
        pool.computeIfAbsent(state, $ -> new PoolEntry<>(this.factory.get())).prepare(state, gui, scale);
    }

    public void end() {
        this.pool.values().removeIf(PoolEntry::closeIfUnused);
    }

    @Override
    public void close() {
        for (PoolEntry<T> entry : pool.values()) {
            entry.renderer.close();
        }
        pool.clear();
    }

    public static final class PoolEntry<T extends PictureInPictureRenderState> {
        private final PictureInPictureRenderer<T> renderer;
        private boolean usedThisFrame = false;

        public PoolEntry(PictureInPictureRenderer<T> renderer) {
            this.renderer = renderer;
        }

        public boolean closeIfUnused() {
            if (!usedThisFrame) {
                renderer.close();
                return true;
            }
            usedThisFrame = false;
            return false;
        }

        public void prepare(T pictureInPictureRenderState, GuiRenderState guiRenderState, int scale) {
            renderer.prepare(pictureInPictureRenderState, guiRenderState, scale);
            usedThisFrame = true;
        }
    }
}
