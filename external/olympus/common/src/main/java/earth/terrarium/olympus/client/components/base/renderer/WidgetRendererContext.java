package earth.terrarium.olympus.client.components.base.renderer;

import net.minecraft.client.gui.components.AbstractWidget;

public final class WidgetRendererContext<T extends AbstractWidget> {

    private final T widget;
    private final int mouseX;
    private final int mouseY;
    private int width;
    private int height;
    private int x;
    private int y;

    public WidgetRendererContext(T widget, int mouseX, int mouseY) {
        this.widget = widget;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.width = widget.getWidth();
        this.height = widget.getHeight();
        this.x = widget.getX();
        this.y = widget.getY();
    }

    public WidgetRendererContext<T> setWidth(int width) {
        this.width = width;
        return this;
    }

    public WidgetRendererContext<T> setHeight(int height) {
        this.height = height;
        return this;
    }

    public WidgetRendererContext<T> setX(int x) {
        this.x = x;
        return this;
    }

    public WidgetRendererContext<T> setY(int y) {
        this.y = y;
        return this;
    }

    public T getWidget() {
        return widget;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLeft() {
        return x;
    }

    public int getRight() {
        return x + width;
    }

    public int getTop() {
        return y;
    }

    public int getBottom() {
        return y + height;
    }

    public int getMiddleX() {
        return x + width / 2;
    }

    public int getMiddleY() {
        return y + height / 2;
    }

    public WidgetRendererContext<T> copy() {
        return new WidgetRendererContext<>(widget, mouseX, mouseY)
                .setWidth(width)
                .setHeight(height)
                .setX(x)
                .setY(y);
    }
}