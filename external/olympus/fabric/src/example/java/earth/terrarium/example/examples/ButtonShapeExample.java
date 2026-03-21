package earth.terrarium.example.examples;

import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.ButtonShapes;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

@OlympusExample(id = "buttonshape", description = "A simple button shape example")
public class ButtonShapeExample extends ExampleScreen {

    @Override
    protected void init() {
        LinearLayout horizontal = LinearLayout.horizontal().spacing(20);

        horizontal.addChild(Widgets.button()
                .withShape(ButtonShapes.ELLIPSE)
                .withRenderer((graphics, context, partialTicks) -> {
                    var side = (int) (context.getWidth() / Mth.SQRT_OF_TWO);

                    graphics.pose().pushMatrix();
                    graphics.pose().translate(context.getMiddleX(), context.getTop());
                    graphics.pose().rotate((float) Math.toRadians(45));
                    graphics.fill(0, 0, side, side, 0xFFFFFFFF);
                    graphics.pose().rotate((float) Math.toRadians(-45));
                    graphics.pose().translate(-side / 2f, (context.getHeight() - side) / 2f);
                    graphics.fill(0, 0, side, side, 0xFFFFFFFF);
                    graphics.pose().popMatrix();
                })
                .withTexture(null)
                .withTooltip(Component.literal("This is a circle button"))
                .withSize(20, 20)
        );

        horizontal.addChild(Widgets.button()
                .withShape(ButtonShapes.DIAMOND)
                .withRenderer((graphics, context, partialTicks) -> {
                    graphics.pose().pushMatrix();
                    graphics.pose().translate(context.getMiddleX(), context.getTop());
                    graphics.pose().rotate((float) Math.toRadians(45));
                    graphics.fill(0, 0, (int) (context.getWidth() / Mth.SQRT_OF_TWO), (int) (context.getWidth() / Mth.SQRT_OF_TWO), 0xFFFFFFFF);
                    graphics.pose().popMatrix();
                })
                .withTexture(null)
                .withTooltip(Component.literal("This is a diamond button"))
                .withSize(20, 20)
        );

        horizontal.arrangeElements();
        FrameLayout.centerInRectangle(horizontal, 0, 0, this.width, this.height);
        horizontal.visitWidgets(this::addRenderableWidget);
    }
}
