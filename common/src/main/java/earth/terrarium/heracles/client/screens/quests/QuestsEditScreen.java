package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.heracles.client.screens.MouseMode;
import earth.terrarium.heracles.client.widgets.SelectableImageButton;
import earth.terrarium.heracles.common.menus.quests.QuestsMenu;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class QuestsEditScreen extends QuestsScreen {

    private SelectableImageButton moveTool;
    private SelectableImageButton dragTool;
    private SelectableImageButton addTool;

    public QuestsEditScreen(QuestsMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        int sidebarWidth = (int) (this.width * 0.25f) - 2;
        addRenderableWidget(new ImageButton(sidebarWidth - 12, 1, 11, 11, 22, 15, 11, HEADING, 256, 256, (button) -> {
            //TODO ADD GROUP
        })).setTooltip(Tooltip.create(Component.literal("Add Group")));

        this.moveTool = addRenderableWidget(new SelectableImageButton(sidebarWidth + 3, 1, 11, 11, 0, 37, 11, HEADING, 256, 256, (button) ->
            updateButtons()
        ));
        this.moveTool.setTooltip(Tooltip.create(Component.literal("Move/Select")));

        this.dragTool = addRenderableWidget(new SelectableImageButton(sidebarWidth + 15, 1, 11, 11, 11, 37, 11, HEADING, 256, 256, (button) ->
            updateButtons()
        ));
        this.dragTool.setTooltip(Tooltip.create(Component.literal("Hand/Drag Tool")));

        this.addTool = addRenderableWidget(new SelectableImageButton(sidebarWidth + 27, 1, 11, 11, 22, 37, 11, HEADING, 256, 256, (button) ->
            updateButtons()
        ));
        this.addTool.setTooltip(Tooltip.create(Component.literal("Add Quest Tool")));

        this.dragTool.setSelected(true);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(stack, partialTick, mouseX, mouseY);
        if (dragTool.isSelected()) {
            setCursor(Cursor.RESIZE_ALL);
        }
    }

    @Override
    protected MouseMode getMouseMode() {
        if (dragTool.isSelected()) {
            return MouseMode.DRAG_MOVE;
        } else if (addTool.isSelected()) {
            return MouseMode.ADD;
        }
        return MouseMode.SELECT_MOVE;
    }

    private void updateButtons() {
        moveTool.setSelected(false);
        dragTool.setSelected(false);
        addTool.setSelected(false);
    }
}