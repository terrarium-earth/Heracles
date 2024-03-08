package earth.terrarium.heracles.client.components.widgets;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ToggleSwitch extends BaseWidget {

    private static final ResourceLocation ON = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/toggle/on.png");
    private static final ResourceLocation OFF = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/toggle/off.png");
    private static final ResourceLocation ON_HOVERED = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/toggle/on_hovered.png");
    private static final ResourceLocation OFF_HOVERED = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/buttons/toggle/off_hovered.png");

    private boolean toggled;

    public ToggleSwitch(boolean value) {
        this();
        this.toggled = value;
    }

    public ToggleSwitch() {
        super(30, 16);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = this.isHovered() ? this.isToggled() ? ON_HOVERED : OFF_HOVERED : this.isToggled() ? ON : OFF;
        graphics.blit(texture, getX(), getY(), 0, 0, 0, this.width, this.height, this.width, height);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.toggled = !this.toggled;
    }

    public boolean isToggled() {
        return this.toggled;
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.POINTER;
    }
}
