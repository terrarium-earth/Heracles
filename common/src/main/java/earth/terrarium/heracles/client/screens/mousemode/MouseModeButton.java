package earth.terrarium.heracles.client.screens.mousemode;

import earth.terrarium.heracles.client.widgets.SelectableImageButton;
import net.minecraft.client.gui.components.WidgetSprites;

public class MouseModeButton extends SelectableImageButton {
    private static String currentType = "move";

    private final String type;

    public MouseModeButton(int x, int y, String type, WidgetSprites sprites, Runnable onSelected) {
        super(x, y, 11, 11, sprites, b -> onSelected.run());
        this.type = type;
    }

    @Override
    public void setSelected(boolean selected) {
        currentType = type;
    }

    @Override
    public boolean isSelected() {
        return currentType.equals(type);
    }
}
