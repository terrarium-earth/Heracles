package earth.terrarium.olympus.client.components.color.type;

import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.elements.GradientGuiElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

public class SaturationBrightnessSelector extends BaseWidget {

    private final HsbState state;

    public SaturationBrightnessSelector(int width, int height, HsbState state) {
        super(width, height);
        this.state = state;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        HsbColor color = state.get();

        int posX = Math.round(color.saturation() * this.getWidth());
        int posY = this.getHeight() - Math.round(color.brightness() * this.getHeight());

        int tileWidth = Math.round(this.getWidth() / 10f);
        int tileHeight = Math.round(this.getHeight() / 10f);

        for (int dy = 0; dy < 10; dy++) {
            float minB = dy / 10f;
            float maxB = (dy + 1) / 10f;
            for (int dx = 0; dx < 10; dx++) {
                float minS = dx / 10f;
                float maxS = (dx + 1) / 10f;

                var element = new GradientGuiElement(
                        HsbColor.of(color.hue(), minS, maxB, 255).toRgba(),
                        HsbColor.of(color.hue(), maxS, maxB, 255).toRgba(),
                        HsbColor.of(color.hue(), minS, minB, 255).toRgba(),
                        HsbColor.of(color.hue(), maxS, minB, 255).toRgba()
                );
                element.submit(
                        graphics,
                        getX() + dx * tileWidth, getY() + (10 - dy - 1) * tileHeight,
                        tileWidth, tileHeight
                );
            }
        }

        graphics.renderOutline(getX() + posX - 1, getY() + posY - 1, 3, 3, 0xFF000000);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (event.input() != 0) return false;
        if (!isMouseOver(event.x(), event.y())) return false;
        int x = (int) event.x() - this.getX();
        int y = (int) event.y() - this.getY();
        if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) return false;
        this.state.set(HsbColor.of(
                this.state.get().hue(),
                Mth.clamp(x / (float) this.getWidth(), 0f, 1f),
                1 - Mth.clamp(y / (float) this.getHeight(), 0f, 1f),
                255
        ));
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return this.mouseClicked(event, false);
    }
}
