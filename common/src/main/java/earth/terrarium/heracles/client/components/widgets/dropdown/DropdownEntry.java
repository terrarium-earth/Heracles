package earth.terrarium.heracles.client.components.widgets.dropdown;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.components.base.ListWidget;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DropdownEntry<T> extends BaseWidget implements ListWidget.Item {

    private static final ResourceLocation ENTRY = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/dropdown/entry.png");
    private static final ResourceLocation ENTRY_HOVERED = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/dropdown/entry_hovered.png");

    private final Dropdown<T> dropdown;
    private final T value;
    private final Runnable action;

    public DropdownEntry(int width, int height, Dropdown<T> dropdown, T value, Runnable action) {
        super(width, height);
        this.action = action;
        this.dropdown = dropdown;
        this.value = value;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = this.isHovered() ? ENTRY_HOVERED : ENTRY;
        UIUtils.blitWithEdge(graphics, texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 3);

        Component text = this.dropdown.getText(this.value);
        int textOffset = (this.height - 8) / 2;
        graphics.drawString(Minecraft.getInstance().font, text, this.getX() + textOffset, this.getY() + textOffset, Dropdown.COLOR);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.action.run();
    }
}
