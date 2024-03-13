package earth.terrarium.heracles.client.ui.quests;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestSettings;
import earth.terrarium.heracles.client.components.AlignedLayout;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestWidget;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.components.widgets.context.ContextMenu;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.screens.quests.QuestSettingsInitalizer;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.ui.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.concurrent.atomic.AtomicInteger;

public class EditQuestsScreen extends AbstractQuestsScreen {

    private final Handler handler = new Handler();

    public EditQuestsScreen(Screen parent, QuestsContent content) {
        super(parent, content);
    }

    @Override
    protected GridLayout initHeader(AtomicInteger column) {
        GridLayout header = super.initHeader(column);

        GridLayout leftButtons = new GridLayout();

        leftButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                .withTooltip(ConstantComponents.TOGGLE_EDIT),
            0, 0,
            leftButtons.newCellSettings().padding(1)
        );

        GridLayout rightButtons = new GridLayout();

        rightButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                .withTooltip(ConstantComponents.TOGGLE_EDIT),
            0, 0,
            rightButtons.newCellSettings().padding(1)
        );

        rightButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.CLOSE, this::onClose)
                .withTooltip(ConstantComponents.CLOSE),
            0, 1,
            rightButtons.newCellSettings().padding(1)
        );

        int half = this.contentWidth / 2;

        header.addChild(
            AlignedLayout.leftAlign(half, HEADER_HEIGHT, leftButtons),
            0, column.getAndIncrement()
        );

        header.addChild(
            AlignedLayout.rightAlign(this.contentWidth - half, HEADER_HEIGHT, rightButtons),
            0, column.getAndIncrement()
        );
        return header;
    }

    @Override
    protected QuestActionHandler handler() {
        return handler;
    }

    private class Handler implements QuestActionHandler {

        private ClientQuests.QuestEntry selected;
        private long lastClick;

        private ClientQuests.QuestEntry dragging;
        private Vector2i start = new Vector2i();
        private Vector2i startOffset = new Vector2i();

        @Override
        public boolean onLeftClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
            if (widget == null) {
                this.dragging = null;
                quests.select(ModUtils.predicateFalse());
                return false;
            }
            ClientQuests.QuestEntry entry = widget.entry();
            if (entry.equals(selected) && System.currentTimeMillis() - lastClick < 500) {
                NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(content.group(), entry.key()));
                this.selected = null;
                this.lastClick = 0;
            } else {
                this.selected = entry;
                this.lastClick = System.currentTimeMillis();
                this.start = new Vector2i((int) mouseX, (int) mouseY);
                this.startOffset = widget.position();
                this.dragging = entry;
                quests.select(questWidget -> questWidget == widget);
            }

            return true;
        }

        @Override
        public boolean onRightClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
            if (widget == null) return false;
            Quest quest = widget.entry().value();
            ContextMenu.open(mouseX, mouseY, 100, menu -> {
                menu.button(Component.literal("Edit Details"), () -> System.out.println("Edit Details"));
                menu.button(Component.literal("Edit Settings"), () -> EditObjectModal.open(
                    QuestSettingsInitalizer.INSTANCE, new ResourceLocation(Heracles.MOD_ID, "quest"),
                    ConstantComponents.Quests.EDIT_SETTINGS, null, quest.settings(), data -> setSettings(widget.entry(), data)
                ));
                menu.divider();
                menu.button(Component.literal("Snap to Grid"), () ->
                    setNewPosition(widget.entry(), widget.position(), true)
                );
                menu.divider();
                menu.dangerButton(ConstantComponents.DELETE, () ->
                    widget.delete(quests)
                );
            });
            return true;
        }

        @Override
        public boolean onRelease(double mouseX, double mouseY, int button) {
            if (this.dragging != null) {
                Vector2i position = dragging.value().display().position(content.group());
                setNewPosition(dragging, position, DisplayConfig.snapToGrid);
                this.dragging = null;
                return true;
            }
            return false;
        }

        @Override
        public TriState onDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (this.dragging != null) {
                Vector2i position = new Vector2i((int) mouseX, (int) mouseY)
                    .sub(this.start)
                    .add(this.startOffset);
                setNewPosition(dragging, position, false);
                return TriState.TRUE;
            }
            return TriState.UNDEFINED;
        }

        private void setNewPosition(ClientQuests.QuestEntry entry, Vector2i position, boolean snapToGrid) {
            ClientQuests.updateQuest(entry, quest -> NetworkQuestData.builder().group(quest, content.group(), pos -> {
                if (snapToGrid) {
                    int newX = Math.abs(position.x() % 32) > 16 ? position.x() / 32 * 32 + 32 : position.x() / 32 * 32;
                    int newY = Math.abs(position.y() % 32) > 16 ? position.y() / 32 * 32 + 32 : position.y() / 32 * 32;
                    pos.x = newX;
                    pos.y = newY;
                } else {
                    pos.x = position.x();
                    pos.y = position.y();
                }
                return pos;
            }));
        }

        private void setSettings(ClientQuests.QuestEntry entry, QuestSettings settings) {
            ClientQuests.updateQuest(entry, quest -> NetworkQuestData.builder()
                .individualProgress(settings.individualProgress())
                .hiddenUntil(settings.hiddenUntil())
                .unlockNotification(settings.unlockNotification())
                .showDependencyArrow(settings.showDependencyArrow())
                .repeatable(settings.repeatable())
            );
        }
    }
}
