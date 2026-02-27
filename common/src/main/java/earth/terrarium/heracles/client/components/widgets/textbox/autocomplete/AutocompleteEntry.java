package earth.terrarium.heracles.client.components.widgets.textbox.autocomplete;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.components.base.ListWidget;
import earth.terrarium.heracles.client.components.widgets.dropdown.Dropdown;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AutocompleteEntry<T> extends BaseWidget implements ListWidget.Item {

    private static final ResourceLocation ENTRY = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/textbox/entry.png");
    private static final ResourceLocation ENTRY_HOVERED = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/textbox/entry_hovered.png");

    private final String value;
    private final Runnable action;

    public AutocompleteEntry(int width, int height, String value, Runnable action) {
        super(width, height);
        this.action = action;
        this.value = value;

        withTooltip(Component.nullToEmpty(value));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture = this.isHovered() ? ENTRY_HOVERED : ENTRY;
        UIUtils.blitWithEdge(graphics, texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 3);
        int textOffset = (this.height - 8) / 2;
        graphics.drawString(Minecraft.getInstance().font, this.value, this.getX() + textOffset, this.getY() + textOffset, Dropdown.COLOR);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.action.run();
    }
}
