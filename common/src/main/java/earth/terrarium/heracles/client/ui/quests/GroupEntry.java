package earth.terrarium.heracles.client.ui.quests;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.components.base.ListWidget;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class GroupEntry extends BaseWidget implements ListWidget.Item {

    private static final ResourceLocation NORMAL = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/groups/normal.png");
    private static final ResourceLocation SELECTED = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/groups/selected.png");
    private static final int PADDING = 4;

    private final String id;
    private final boolean selected;

    public GroupEntry(int width, int height, String id, boolean selected) {
        super(width, height);
        this.id = id;
        this.selected = selected;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered() || this.selected) {
            ResourceLocation texture = this.selected ? SELECTED : NORMAL;
            UIUtils.blitWithEdge(graphics, texture, getX(), getY(), getWidth(), getHeight(), 4);
            CursorUtils.setCursor(this.isHovered() && !this.selected, CursorScreen.Cursor.POINTER);
        }
        graphics.drawString(
            Minecraft.getInstance().font,
            this.id,
            this.getX() + PADDING, this.getY() + ((this.getHeight() - 10) / 2) + 1,
            0xFFFFFF
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(this.id));
    }
}
