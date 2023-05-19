package earth.terrarium.heracles.api.client.settings;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public interface SettingInitializer<T> {

    CreationData create(@Nullable T object);

    T create(String id, T object, Data data);

    record CreationData(Map<String, Pair<?, Setting<?, ?>>> data) {

        public CreationData() {
            this(new LinkedHashMap<>());
        }

        public <A, B extends Renderable & LayoutElement> void put(String id, Setting<A, B> setting, A value) {
            data.put(id, Pair.of(value, setting));
        }

        public Renderable get(int width, String id) {
            return data.get(id).getSecond().createWidget(width, ModUtils.cast(data.get(id).getFirst()));
        }
    }

    record Data(Map<String, Renderable> settings) {

        public Data() {
            this(new LinkedHashMap<>());
        }

        public void put(String id, GuiEventListener widget) {
            settings.put(id, (Renderable) widget);
        }

        @SuppressWarnings("unchecked")
        public <A, B extends Renderable & LayoutElement> Optional<A> get(String id, Setting<A, B> setting) {
            var widget = settings.get(id);
            if (!(widget instanceof GuiEventListener)) {
                return Optional.empty();
            }
            return Optional.ofNullable(setting.getValue((B) widget));
        }
    }
}
