package earth.terrarium.heracles.api.client.settings;

import net.minecraft.client.gui.components.AbstractWidget;
import org.jetbrains.annotations.Nullable;

public interface Setting<T, E extends AbstractWidget> {

    E createWidget(@Nullable E old, int width, T value);

    T getValue(E widget);

}
