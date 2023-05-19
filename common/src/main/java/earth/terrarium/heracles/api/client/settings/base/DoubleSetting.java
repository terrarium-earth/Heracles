package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.boxes.DoubleEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;

public record DoubleSetting(double defaultValue) implements Setting<Double, DoubleEditBox> {

    public static final DoubleSetting INSTANCE = new DoubleSetting(0);

    @Override
    public DoubleEditBox createWidget(int width, Double value) {
        final DoubleEditBox box = new DoubleEditBox(Minecraft.getInstance().font, 0, 0, width, 11, CommonComponents.EMPTY);
        box.setIfNotFocused(value == null ? defaultValue : value);
        return box;
    }

    @Override
    public Double getValue(DoubleEditBox widget) {
        return widget.getDoubleValue().orElse(defaultValue);
    }
}
