package earth.terrarium.heracles.api.client.settings;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;

public interface Setting<T, E extends Renderable & LayoutElement> {

    E createWidget(int width, T value);

    T getValue(E widget);

}
