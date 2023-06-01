package earth.terrarium.heracles.api.client.settings.base;

import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.buttons.EnumButton;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public record BooleanSetting(
    boolean defaultValue
) implements Setting<Boolean, EnumButton<BooleanSetting.BooleanState>> {

    public static final BooleanSetting FALSE = new BooleanSetting(false);
    public static final BooleanSetting TRUE = new BooleanSetting(true);

    @Override
    public EnumButton<BooleanSetting.BooleanState> createWidget(int width, Boolean value) {
        return new EnumButton<>(0, 0, width, 20, BooleanState.class, value ? BooleanState.TRUE : BooleanState.FALSE);
    }

    @Override
    public Boolean getValue(EnumButton<BooleanState> widget) {
        return widget.value() == BooleanState.TRUE;
    }

    public enum BooleanState implements StringRepresentable {
        TRUE,
        FALSE;

        @Override
        public @NotNull String getSerializedName() {
            return this.name().charAt(0) + this.name().substring(1).toLowerCase();
        }
    }
}
