package earth.terrarium.heracles.client.widgets;

import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.WidgetSprites;

public class ToggleImageButton extends StateImageButton implements ThemedButton {

    public ToggleImageButton(int x, int y, int width, int height, WidgetSprites sprites, BooleanConsumer onPress) {
        super(
            x, y,
            width, height,
            sprites,
            2, i -> onPress.accept(i == 1)
        );
    }
}
