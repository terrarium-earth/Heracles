package earth.terrarium.example.examples;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.UIIcons;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

@OlympusExample(id = "button", description = "A simple button example")
public class ButtonExample extends ExampleScreen {

    @Override
    protected void init() {
        LinearLayout horizontal = LinearLayout.horizontal().spacing(20);

        Color green = new Color(0xFF559955);

        horizontal.addChild(Widgets.button()
                .withCallback(() -> System.out.println("Button 1 clicked"))
                .withRenderer(WidgetRenderers.text(Component.literal("Button 1"))
                        .withColor(MinecraftColors.RED)
                        .withShadow()
                )
                .withTexture(UIConstants.DANGER_BUTTON)
                .withTooltip(Component.literal("This is a button tooltip"))
                .withSize(100, 20)
        );

        horizontal.addChild(Widgets.button()
                .withCallback(() -> System.out.println("Button 2 clicked"))
                .withRenderer(WidgetRenderers.icon(UIIcons.MODRINTH)
                        .withShadow()
                        .withPadding(3, 4, 5, 4)
                )
                .withTexture(UIConstants.PRIMARY_BUTTON)
                .withTooltip(Component.literal("This is a button tooltip"))
                .withSize(20, 20)
        );

        horizontal.addChild(Widgets.button()
                .withCallback(() -> System.out.println("Button 3 clicked"))
                .withRenderer(WidgetRenderers.text(Component.literal("Button 1"))
                        .withColor(green)
                        .withShadow()
                )
                .withTooltip(Component.literal("This is a button tooltip"))
                .withSize(100, 20)
        );

        horizontal.addChild(Widgets.button()
                .withCallback(() -> System.out.println("Button 4 clicked"))
                .withRenderer(WidgetRenderers.icon(UIIcons.MODRINTH)
                        .withColor(green)
                        .withShadow()
                        .withPadding(3, 4, 5, 4)
                )
                .withTooltip(Component.literal("This is a button tooltip"))
                .withSize(21, 21)
        );

        horizontal.addChild(Widgets.button()
                .withCallback(() -> System.out.println("Button 5 clicked"))
                .withRenderer((graphics, context, partialTicks) -> graphics.fill(
                        context.getLeft(), context.getTop(),
                        context.getRight(), context.getBottom(),
                        context.getWidget().isHovered() ? 0x20000000 : 0x80000000
                ))
                .withTooltip(Component.literal("This is a button tooltip"))
                .withSize(20, 20)
                .asDisabled()
        );

        horizontal.arrangeElements();
        FrameLayout.centerInRectangle(horizontal, 0, 0, this.width, this.height);
        horizontal.visitWidgets(this::addRenderableWidget);
    }
}
