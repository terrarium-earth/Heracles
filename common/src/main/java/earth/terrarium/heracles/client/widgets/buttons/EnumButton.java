package earth.terrarium.heracles.client.widgets.buttons;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public class EnumButton<T extends Enum<T> & StringRepresentable> extends Button {

    private final T[] values;
    private T value;

    public EnumButton(int x, int y, int width, int height, Class<T> enumClass, T value) {
        super(x, y, width, height, CommonComponents.EMPTY, b -> {}, DEFAULT_NARRATION);
        this.values = enumClass.getEnumConstants();
        this.value = value;
    }

    @Override
    public void onPress() {
        int index = this.value.ordinal() + 1;
        if (index >= this.values.length) {
            index = 0;
        }
        this.value = this.values[index];
    }

    @Override
    public @NotNull Component getMessage() {
        return Component.literal(this.value.getSerializedName());
    }

    public T value() {
        return this.value;
    }
}
