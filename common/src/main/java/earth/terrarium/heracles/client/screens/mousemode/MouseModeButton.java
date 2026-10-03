package earth.terrarium.heracles.client.screens.mousemode;

import earth.terrarium.heracles.client.widgets.SelectableImageButton;
import net.minecraft.client.gui.components.WidgetSprites;

public class MouseModeButton extends SelectableImageButton {

    private static MouseButtonType currentType = MouseButtonType.MOVE;

    private final MouseButtonType type;

    public MouseModeButton(int x, int y, MouseButtonType type, WidgetSprites sprites, Runnable onSelected) {
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

    public enum MouseButtonType {
        MOVE,
        DRAG,
        ADD,
        LINK;
    }
}
