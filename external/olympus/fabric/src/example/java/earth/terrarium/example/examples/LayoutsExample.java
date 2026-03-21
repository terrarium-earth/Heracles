package earth.terrarium.example.examples;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.compound.LayoutWidget;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.minecraft.client.gui.components.AbstractWidget;

@OlympusExample(id = "layouts", description = "A simple example of using layouts")
public class LayoutsExample extends ExampleScreen {

    private static AbstractWidget[] createButtons() {
        AbstractWidget[] buttons = new AbstractWidget[12];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = Widgets.button().withSize(20, 20);
        }
        return buttons;
    }

    @Override
    protected void init() {
        Layouts.rows(5)
                .withGap(5)
                .withChildren(createButtons())
                .withChild(Widgets.button().withTexture(UIConstants.PRIMARY_BUTTON).withSize(20, 20))
                .withPosition(0, 0)
                .build(this::addRenderableWidget);

        Layouts.columns(5)
                .withGap(5)
                .withChildren(createButtons())
                .withChild(Widgets.button().withTexture(UIConstants.PRIMARY_BUTTON).withSize(20, 20))
                .withPosition(200, 0)
                .build(this::addRenderableWidget);

        var test = new LayoutWidget<>(Layouts.columns(5));
        test.withContents(gridViewLayout -> {
                gridViewLayout.withGap(5);
                gridViewLayout.withChildren(createButtons());
                gridViewLayout.withChildren(createButtons());
                gridViewLayout.withChild(Widgets.button().withTexture(UIConstants.PRIMARY_BUTTON).withSize(20, 20));
            })
            .withScrollable(TriState.TRUE, TriState.TRUE)
            .withTexture(UIConstants.MODAL_INSET)
            .withScrollbarBackground(UIConstants.MODAL_INSET)
            .withOverscroll(4, 4)
            .withContentMargin(1)
            .withSize(100, 100);

        addRenderableWidget(test.withPosition(300, 100));

        Layouts.layout()
                .withGap(5)
                .withRow(createButtons())
                .withRow(createButtons())
                .withRow(Widgets.button().withTexture(UIConstants.PRIMARY_BUTTON).withSize(20, 20))
                .withPosition(0, 150)
                .build(this::addRenderableWidget);
    }
}
