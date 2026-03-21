package earth.terrarium.example.examples;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.ui.UIIcons;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.network.chat.Component;

@OlympusExample(id = "icons", description = "Mod Icons")
public class IconExample extends ExampleScreen {

    @Override
    protected void init() {
        var icons = UIIcons.getIcons();
        int iconsPerRow = (int) (Math.sqrt(icons.size()) + 1);


        var layout = Layouts.layout().withGap(2);

        for (int i = 0; i < icons.size(); i += iconsPerRow) {
            var rowIcons = icons.subList(i, Math.min(i + iconsPerRow, icons.size()));
            AbstractWidget[] row = new AbstractWidget[rowIcons.size()];
            for (int j = 0; j < rowIcons.size(); j++) {
                row[j] = Widgets.button()
                        .withRenderer(
                                WidgetRenderers.layered(
                                        WidgetRenderers.withColors(
                                                WidgetRenderers.solid(),
                                                Color.DEFAULT,
                                                Color.DEFAULT,
                                                new Color(0x7F000000)
                                        ),
                                        WidgetRenderers.icon(rowIcons.get(j)).withColor(Color.DEFAULT).withCentered(12, 12)
                                )
                        )
                        .withTexture(null)
                        .withTooltip(Component.literal(rowIcons.get(j).toString()))
                        .withSize(16, 16);
            }
            layout.withRow(row);
        }

        FrameLayout.centerInRectangle(layout.build(this::addRenderableWidget), 0, 0, this.width, this.height);
    }
}
