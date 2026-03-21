package earth.terrarium.olympus.client.components.color.type;

import earth.terrarium.olympus.client.components.base.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

public class AlphaSelector extends BaseWidget {

    private final HsbState state;

    public AlphaSelector(int width, int height, HsbState state) {
        super(width, height);
        this.state = state;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        HsbColor color = state.get();

        int size = Math.min(getWidth(), getHeight()) / 2;
        for (int x = 0; x < Math.ceilDiv(getWidth(), size); x++) {
            for (int y = 0; y < Math.ceilDiv(getHeight(), size); y++) {
                graphics.fill(
                        getX() + x * size, getY() + y * size,
                        getX() + (x + 1) * size, getY() + (y + 1) * size,
                        (y + x) % 2 == 0 ? 0xFFDEDEDE : 0xFFFFFFFF
                );
            }
        }

        for (int i = 0; i < getWidth(); i++) {
            int alpha = Math.round(((float)i / (float) getWidth()) * 255f);
            graphics.fill(
                    getX() + i, getY(),
                    getX() + i + 1, getY() + getHeight(),
                    Mth.hsvToArgb(color.hue(), color.saturation(), color.brightness(), alpha)
            );
        }

        int posX = Mth.floor((this.state.get().alpha() / 255f) * (this.getWidth() - 1));
        graphics.renderOutline(getX() + posX - 1, getY(), 3, getHeight(), 0xFF000000);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (event.input() != 0) return false;
        if (!isMouseOver(event.x(), event.y())) return false;
        float alpha = Mth.clamp((float) (event.x() - getX()) / (float) getWidth(), 0f, 1f);
        this.state.set(this.state.get().withAlpha(Mth.ceil(alpha * 255f)));
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return mouseClicked(event, false);
    }
}
