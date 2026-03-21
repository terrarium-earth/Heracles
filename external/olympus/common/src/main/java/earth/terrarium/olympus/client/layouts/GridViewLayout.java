package earth.terrarium.olympus.client.layouts;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;

import java.util.function.Consumer;

public class GridViewLayout extends BaseLayout<ViewLayout> {

    private final LinearLayout.Orientation orientation;
    private final int amount;
    private int size;

    GridViewLayout(LinearLayout.Orientation orientation, int amount) {
        super(new ViewLayout());
        this.orientation = orientation;
        this.amount = amount;
    }

    public GridViewLayout withGap(int gap) {
        this.layout.withGap(gap);
        return this;
    }

    public GridViewLayout withChild(LayoutElement element) {
        switch (this.orientation) {
            case HORIZONTAL -> {
                if (this.size % this.amount == 0) {
                    this.layout.withRow();
                    this.size = 0;
                }
                this.layout.get(this.layout.rows() - 1).addChild(element);
                this.size++;
            }
            case VERTICAL -> {
                if (this.layout.rows() < this.amount && this.size == this.layout.rows()) {
                    this.layout.withRow();
                }
                this.layout.get(this.size % this.amount).addChild(element);
            }
        }
        this.size++;
        return this;
    }

    public GridViewLayout withChildren(LayoutElement... elements) {
        for (LayoutElement element : elements) {
            withChild(element);
        }
        return this;
    }

    @Override
    public GridViewLayout withPosition(int x, int y) {
        return (GridViewLayout) super.withPosition(x, y);
    }

    @Override
    public GridViewLayout build(Consumer<AbstractWidget> adder) {
        return (GridViewLayout) super.build(adder);
    }
}
