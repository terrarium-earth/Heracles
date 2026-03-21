package earth.terrarium.olympus.client.layouts;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ViewLayout extends BaseLayout<LinearLayout> {

    private final List<LinearLayout> rows = new ArrayList<>();
    private int gap;

    ViewLayout() {
        super(LinearLayout.vertical());
    }

    public ViewLayout withGap(int gap) {
        this.gap = gap;
        this.layout.spacing(gap);
        return this;
    }

    public ViewLayout withRow(LayoutElement... elements) {
        LinearLayout row = LinearLayout.horizontal().spacing(this.gap);
        for (LayoutElement element : elements) {
            row.addChild(element);
        }
        this.layout.addChild(row);
        this.rows.add(row);
        return this;
    }

    @Override
    public ViewLayout withPosition(int x, int y) {
        return (ViewLayout) super.withPosition(x, y);
    }

    @Override
    public ViewLayout build(Consumer<AbstractWidget> adder) {
        return (ViewLayout) super.build(adder);
    }
    
    public LinearLayout get(int index) {
        return this.rows.get(index);
    }

    public int rows() {
        return this.rows.size();
    }
}
