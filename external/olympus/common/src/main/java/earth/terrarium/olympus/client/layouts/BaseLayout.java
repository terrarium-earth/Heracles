package earth.terrarium.olympus.client.layouts;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class BaseLayout<T extends Layout> implements Layout {

    protected final T layout;

    public BaseLayout(T layout) {
        this.layout = layout;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        this.layout.visitWidgets(consumer);
    }

    @Override
    public void arrangeElements() {
        this.layout.arrangeElements();
    }

    @Override
    public @NotNull ScreenRectangle getRectangle() {
        return this.layout.getRectangle();
    }

    @Override
    public void setPosition(int x, int y) {
        this.layout.setPosition(x, y);
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> visitor) {
        this.layout.visitChildren(visitor);
    }

    @Override
    public void setX(int x) {
        this.layout.setX(x);
    }

    @Override
    public void setY(int y) {
        this.layout.setY(y);
    }

    @Override
    public int getX() {
        return this.layout.getX();
    }

    @Override
    public int getY() {
        return this.layout.getY();
    }

    @Override
    public int getWidth() {
        return this.layout.getWidth();
    }

    @Override
    public int getHeight() {
        return this.layout.getHeight();
    }

    public BaseLayout<T> withPosition(int x, int y) {
        this.layout.setPosition(x, y);
        return this;
    }

    public BaseLayout<T> build(Consumer<AbstractWidget> adder) {
        this.layout.arrangeElements();
        this.layout.visitWidgets(adder);
        return this;
    }
}
