package earth.terrarium.heracles.client.screens.mousemode;

import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.widgets.SelectableImageButton;

public class MouseModeButton extends SelectableImageButton {

    private static MouseButtonType currentType = MouseButtonType.MOVE;

    private final MouseButtonType type;

    public MouseModeButton(int x, int y, MouseButtonType type, Runnable onSelected) {
        super(x, y, 11, 11, type.u(), type.v(), 11, AbstractQuestScreen.HEADING, 256, 256, b -> onSelected.run());
        this.type = type;
    }

    @Override
    public void setSelected(boolean selected) {
        currentType = type;
    }

    @Override
    public boolean isSelected() {
        return currentType == type;
    }
}
