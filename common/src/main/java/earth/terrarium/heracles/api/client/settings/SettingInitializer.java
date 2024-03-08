package earth.terrarium.heracles.api.client.settings;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
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

        public <A, B extends AbstractWidget> void put(String id, Setting<A, B> setting, A value) {
            data.put(id, Pair.of(value, setting));
        }

        public AbstractWidget get(@Nullable AbstractWidget old, int width, String id) {
            return data.get(id).getSecond().createWidget(ModUtils.cast(old), width, ModUtils.cast(data.get(id).getFirst()));
        }

        public boolean isEmpty() {
            return data.isEmpty();
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
        public <A, B extends AbstractWidget> Optional<A> get(String id, Setting<A, B> setting) {
            var widget = settings.get(id);
            if (!(widget instanceof GuiEventListener)) {
                return Optional.empty();
            }
            return Optional.ofNullable(setting.getValue((B) widget));
        }
    }
}
