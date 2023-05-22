package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.buttons.EnumButton;
import net.minecraft.util.StringRepresentable;

public record EnumSetting<T extends Enum<T> & StringRepresentable>(Class<T> enumClass,
                                                                   T defaultValue) implements Setting<T, EnumButton<T>> {

    @Override
    public EnumButton<T> createWidget(int width, T value) {
        return new EnumButton<>(0, 0, width, 11, enumClass, value);
    }

    @Override
    public T getValue(EnumButton<T> widget) {
        return widget.value();
    }
}
