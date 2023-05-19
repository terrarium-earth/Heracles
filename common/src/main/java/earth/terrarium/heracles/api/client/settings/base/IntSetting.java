package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.boxes.IntEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;

public record IntSetting(int defaultValue) implements Setting<Integer, IntEditBox> {

    public static final IntSetting ZERO = new IntSetting(0);
    public static final IntSetting ONE = new IntSetting(1);

    @Override
    public IntEditBox createWidget(int width, Integer value) {
        final IntEditBox box = new IntEditBox(Minecraft.getInstance().font, 0, 0, width, 11, CommonComponents.EMPTY);
        box.setIfNotFocused(value == null ? defaultValue : value);
        return box;
    }

    @Override
    public Integer getValue(IntEditBox widget) {
        return widget.getIntValue().orElse(defaultValue);
    }
}
