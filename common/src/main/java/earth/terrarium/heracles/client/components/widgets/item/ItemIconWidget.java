package earth.terrarium.heracles.client.components.widgets.item;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.utils.ItemValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.CommonInputs;

public class ItemIconWidget extends BaseWidget {

    private final ItemValue value;
    private final Runnable onClick;

    public ItemIconWidget(ItemValue value, Runnable onClick) {
        super(16, 16);

        this.value = value;
        this.onClick = onClick;

        withTooltip(value.getDisplayName());
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHoveredOrFocused()) {
            UIUtils.blitWithEdge(graphics, UIConstants.ITEM_BACKGROUND, this.getX() - 1, this.getY() - 1, this.getWidth() + 2, this.getHeight() + 2, 3);
        }
        graphics.renderFakeItem(this.value.getDefaultInstance(), this.getX(), this.getY());
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.playDownSound(Minecraft.getInstance().getSoundManager());
        this.onClick.run();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active || !this.visible) {
            return false;
        } else if (CommonInputs.selected(keyCode)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onClick.run();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.POINTER;
    }
}
