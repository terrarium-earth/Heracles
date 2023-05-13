package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.heracles.client.screens.MouseMode;
import earth.terrarium.heracles.client.widgets.CreateGroupModal;
import earth.terrarium.heracles.client.widgets.SelectableImageButton;
import earth.terrarium.heracles.client.widgets.icon.IconModal;
import earth.terrarium.heracles.client.widgets.icon.background.IconBackgroundModal;
import earth.terrarium.heracles.client.widgets.upload.UploadModal;
import earth.terrarium.heracles.common.menus.quests.QuestsMenu;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class QuestsEditScreen extends QuestsScreen {

    private SelectableImageButton moveTool;
    private SelectableImageButton dragTool;
    private SelectableImageButton addTool;
    private SelectableImageButton linkTool;

    private UploadModal uploadModal;
    private CreateGroupModal groupModal;
    private IconBackgroundModal iconBackgroundModal;
    private IconModal iconModal;

    public QuestsEditScreen(QuestsMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
        int sidebarWidth = (int) (this.width * 0.25f) - 2;
        addRenderableWidget(new ImageButton(sidebarWidth - 12, 1, 11, 11, 22, 15, 11, HEADING, 256, 256, (button) -> {
            if (this.groupModal != null) {
                this.groupModal.setVisible(true);
            }
        })).setTooltip(Tooltip.create(Component.literal("Add Group")));

        selectQuestWidget = new SelectQuestWidget(
            (int) (this.width * 0.75f) + 2,
            15,
            (int) (this.width * 0.25f) - 2,
            this.height - 15
        );

        this.moveTool = addRenderableWidget(new SelectableImageButton(sidebarWidth + 3, 1, 11, 11, 0, 37, 11, HEADING, 256, 256, (button) -> {
            updateButtons();
            if (questsWidget.selectHandler().selectedQuest() != null) {
                if (!actualChildren().contains(selectQuestWidget)) {
                    addRenderableWidget(selectQuestWidget);
                }
                selectQuestWidget.setEntry(questsWidget.selectHandler().selectedQuest().entry());
            }
        }));
        this.moveTool.setTooltip(Tooltip.create(Component.literal("Move/Select [V]")));

        this.dragTool = addRenderableWidget(new SelectableImageButton(sidebarWidth + 15, 1, 11, 11, 11, 37, 11, HEADING, 256, 256, (button) -> {
            updateButtons();
            clearWidget();
        }));
        this.dragTool.setTooltip(Tooltip.create(Component.literal("Hand/Drag Tool [H]")));

        this.addTool = addRenderableWidget(new SelectableImageButton(sidebarWidth + 27, 1, 11, 11, 22, 37, 11, HEADING, 256, 256, (button) -> {
            updateButtons();
            clearWidget();
        }));
        this.addTool.setTooltip(Tooltip.create(Component.literal("Add Quest Tool [U]")));

        this.linkTool = addRenderableWidget(new SelectableImageButton(sidebarWidth + 39, 1, 11, 11, 0, 59, 11, HEADING, 256, 256, (button) -> {
            updateButtons();
            clearWidget();
        }));
        this.linkTool.setTooltip(Tooltip.create(Component.literal("Link Tool [L]")));

        addRenderableWidget(new ImageButton(this.width - 36, 1, 11, 11, 33, 37, 11, HEADING, 256, 256, (button) -> {
            if (this.uploadModal != null) {
                this.uploadModal.setVisible(true);
            }
        })).setTooltip(Tooltip.create(Component.literal("Import Quests")));

        this.dragTool.setSelected(true);

        this.uploadModal = addTemporary(new UploadModal(this.width, this.height));
        this.groupModal = addTemporary(new CreateGroupModal(this.width, this.height));
        this.iconBackgroundModal = addTemporary(new IconBackgroundModal(this.width, this.height));
        this.iconModal = addTemporary(new IconModal(this.width, this.height));
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(stack, partialTick, mouseX, mouseY);
        if (dragTool.isSelected()) {
            setCursor(Cursor.RESIZE_ALL);
        }
        if (addTool.isSelected()) {
            setCursor(Cursor.CROSSHAIR);
        }
    }

    @Override
    protected MouseMode getMouseMode() {
        if (dragTool.isSelected()) {
            return MouseMode.DRAG_MOVE;
        } else if (addTool.isSelected()) {
            return MouseMode.ADD;
        } else if (linkTool.isSelected()) {
            return MouseMode.SELECT_LINK;
        }
        return MouseMode.SELECT_MOVE;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean result = super.keyPressed(keyCode, scanCode, modifiers);
        if (result) {
            return true;
        }
        return switch (keyCode) {
            case InputConstants.KEY_V -> {
                updateButtons();
                moveTool.setSelected(true);
                yield true;
            }
            case InputConstants.KEY_H -> {
                updateButtons();
                dragTool.setSelected(true);
                yield true;
            }
            case InputConstants.KEY_U -> {
                updateButtons();
                addTool.setSelected(true);
                yield true;
            }
            case InputConstants.KEY_L -> {
                updateButtons();
                linkTool.setSelected(true);
                yield true;
            }
            default -> false;
        };
    }

    @Override
    public void removed() {
        super.removed();
        uploadModal.setVisible(false);
        ClientQuests.sendDirty();
    }

    private void clearWidget() {
        if (selectQuestWidget != null) {
            selectQuestWidget.setEntry(null);
            removeWidget(selectQuestWidget);
        }
    }

    private void updateButtons() {
        moveTool.setSelected(false);
        dragTool.setSelected(false);
        addTool.setSelected(false);
        linkTool.setSelected(false);
    }

    public IconBackgroundModal iconBackgroundModal() {
        return this.iconBackgroundModal;
    }

    public IconModal iconModal() {
        return this.iconModal;
    }
}