package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.api.quests.QuestSettings;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.MouseMode;
import earth.terrarium.heracles.client.utils.MouseClick;
import earth.terrarium.heracles.client.widgets.SelectableImageButton;
import earth.terrarium.heracles.client.widgets.modals.AddDependencyModal;
import earth.terrarium.heracles.client.widgets.modals.TextInputModal;
import earth.terrarium.heracles.client.widgets.modals.icon.IconModal;
import earth.terrarium.heracles.client.widgets.modals.icon.background.IconBackgroundModal;
import earth.terrarium.heracles.client.widgets.modals.upload.UploadModal;
import earth.terrarium.heracles.common.menus.quests.QuestsMenu;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.CreateGroupPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;
import java.util.HashSet;

public class QuestsEditScreen extends QuestsScreen {

    private SelectableImageButton moveTool;
    private SelectableImageButton dragTool;
    private SelectableImageButton addTool;
    private SelectableImageButton linkTool;

    private UploadModal uploadModal;
    private TextInputModal<Unit> groupModal;
    private IconBackgroundModal iconBackgroundModal;
    private IconModal iconModal;
    private AddDependencyModal dependencyModal;
    private TextInputModal<MouseClick> questModal;

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
            this.height - 15,
            this.questsWidget
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
        this.groupModal = addTemporary(new TextInputModal<>(this.width, this.height, Component.literal("Create Group"), (ignored, text) -> {
            NetworkHandler.CHANNEL.sendToServer(new CreateGroupPacket(text));
            ClientQuests.groups().add(text);
            if (Minecraft.getInstance().screen instanceof QuestsScreen screen) {
                screen.getGroupsList().addGroup(text);
            }
        }, text -> !ClientQuests.groups().contains(text.trim())));
        this.iconBackgroundModal = addTemporary(new IconBackgroundModal(this.width, this.height));
        this.iconModal = addTemporary(new IconModal(this.width, this.height));
        this.dependencyModal = addTemporary(new AddDependencyModal(this.width, this.height));
        this.questModal = addTemporary(new TextInputModal<>(this.width, this.height, Component.literal("Create Quest"), (position, text) -> {
            MouseClick local = this.questsWidget.getLocal(position);
            QuestDisplay display = QuestDisplay.createDefault();
            display.position(this.menu.group()).set((int) local.x() - 12, (int) local.y() - 12);
            Quest quest = new Quest(
                display,
                QuestSettings.createDefault(),
                new HashSet<>(),
                new HashMap<>(),
                new HashMap<>()
            );
            this.questsWidget.addQuest(ClientQuests.addQuest(text, quest));
        }, text -> ClientQuests.get(text.trim()).isEmpty()));
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isTemporaryWidgetVisible() && getMouseMode() == MouseMode.ADD && this.questsWidget.isMouseOver(mouseX, mouseY)) {
            this.questModal.setVisible(true);
            this.questModal.setData(new MouseClick(mouseX, mouseY, button));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (selectQuestWidget.keyPressed(keyCode, scanCode, modifiers)) {
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

    public AddDependencyModal dependencyModal() {
        return this.dependencyModal;
    }

    public TextInputModal<MouseClick> questModal() {
        return this.questModal;
    }
}