package earth.terrarium.olympus.client.pipelines.uniforms;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.renderer.DynamicUniformStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RenderPipelineUniformsStorage {

    private static final List<DynamicUniformStorage<?>> storage = new ArrayList<>();

    public static <T extends DynamicUniformStorage.DynamicUniform> Supplier<DynamicUniformStorage<T>> register(
            String name,
            int capacity,
            Std140SizeCalculator size
    ) {
        return Suppliers.memoize(() -> {
            var storage = new DynamicUniformStorage<T>(name, size.get(), capacity);
            RenderPipelineUniformsStorage.storage.add(storage);
            return storage;
        });
    }

    public static void endFrame() {
        for (var uniformStorage : storage) {
            uniformStorage.endFrame();
        }
    }
}
