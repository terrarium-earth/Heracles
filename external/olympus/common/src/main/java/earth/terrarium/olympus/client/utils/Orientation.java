package earth.terrarium.olympus.client.utils;

import net.minecraft.client.gui.layouts.LayoutElement;

public enum Orientation {
    HORIZONTAL,
    VERTICAL;

    public int getValue(int horizontal, int vertical) {
        return switch (this) {
            case HORIZONTAL -> horizontal;
            case VERTICAL -> vertical;
        };
    }

    public int getSize(LayoutElement element) {
        return getValue(element.getWidth(), element.getHeight());
    }

    public void setPos(LayoutElement element, int value) {
        switch (this) {
            case HORIZONTAL -> element.setX(value);
            case VERTICAL -> element.setY(value);
        }
    }
}
