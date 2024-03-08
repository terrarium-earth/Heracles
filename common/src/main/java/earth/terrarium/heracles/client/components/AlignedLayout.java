package earth.terrarium.heracles.client.components;

import earth.terrarium.hermes.api.Alignment;
import net.minecraft.client.gui.layouts.AbstractLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;

import java.util.function.Consumer;

public class AlignedLayout extends AbstractLayout {

    private final Alignment alignmentX;
    private final Alignment alignmentY;
    private final Layout layout;

    public AlignedLayout(int width, int height, Alignment alignmentX, Alignment alignmentY, Layout layout) {
        super(0, 0, width, height);
        this.alignmentX = alignmentX;
        this.alignmentY = alignmentY;
        this.layout = layout;
    }

    public static AlignedLayout leftAlign(int width, int height, Layout layout) {
        return new AlignedLayout(width, height, Alignment.MIN, Alignment.MIDDLE, layout);
    }

    public static AlignedLayout rightAlign(int width, int height, Layout layout) {
        return new AlignedLayout(width, height, Alignment.MAX, Alignment.MIDDLE, layout);
    }

    @Override
    public void visitChildren(Consumer<LayoutElement> consumer) {
        consumer.accept(this.layout);
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();

        int x = getX() + Alignment.getOffset(this.width, this.layout.getWidth(), this.alignmentX);
        int y = getY() + Alignment.getOffset(this.height, this.layout.getHeight(), this.alignmentY);

        this.layout.setPosition(x, y);
    }
}
