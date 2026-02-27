package earth.terrarium.heracles.client.ui.quests;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestSettings;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestWidget;
import earth.terrarium.heracles.client.components.quests.QuestsWidget;
import earth.terrarium.heracles.client.components.widgets.context.ContextMenu;
import earth.terrarium.heracles.client.handlers.ClientQuestNetworking;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.ui.UIComponents;
import earth.terrarium.heracles.client.ui.modals.CreateQuestModal;
import earth.terrarium.heracles.client.ui.modals.EditObjectModal;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

class EditActionHandler implements QuestActionHandler {

    private static final ResourceLocation QUEST = new ResourceLocation(Heracles.MOD_ID, "quest");

    private final Supplier<QuestsWidget> quests;
    private final QuestsContent content;

    private ClientQuests.QuestEntry selected;
    private long lastClick;

    private ClientQuests.QuestEntry dragging;
    private Vector2i start = new Vector2i();
    private Vector2i startOffset = new Vector2i();

    EditActionHandler(Supplier<QuestsWidget> quests, QuestsContent content) {
        this.quests = quests;
        this.content = content;
    }

    @Override
    public boolean onLeftClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
        if (widget == null) {
            this.dragging = null;
            this.quests.get().select(ModUtils.predicateFalse());
            this.selected = null;
            return false;
        }
        ClientQuests.QuestEntry entry = widget.entry();
        if (entry.equals(this.selected) && System.currentTimeMillis() - this.lastClick < 500) {
            this.open();
        } else {
            this.selected = entry;
            this.lastClick = System.currentTimeMillis();
            this.start = new Vector2i((int) mouseX, (int) mouseY);
            this.startOffset = widget.position();
            this.dragging = entry;
            this.quests.get().select(questWidget -> questWidget == widget);
        }

        return true;
    }

    @Override
    public boolean onRightClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
        ContextMenu.open(mouseX, mouseY, menu -> {
            if (widget != null) {
                Quest quest = widget.entry().value();
                menu.button(UIComponents.EDIT_DETAILS, () -> EditObjectModal.open(
                    QuestDetailsInitializer.INSTANCE, QUEST, UIComponents.EDIT_DETAILS, null,
                    new QuestDetailsInitializer.Details(quest), data -> setDetails(widget.entry(), data)
                ));
                menu.button(UIComponents.EDIT_SETTINGS, () -> EditObjectModal.open(
                    QuestSettingsInitializer.INSTANCE, QUEST, UIComponents.EDIT_SETTINGS, null,
                    quest.settings(), data -> setSettings(widget.entry(), data)
                ));
                menu.divider();
                menu.button(UIComponents.SNAP_TO_GRID, () ->
                    setNewPosition(widget.entry(), widget.position(), true)
                );
                if (this.selected != null && this.selected != widget.entry()) {
                    ClientQuests.QuestEntry dependency = widget.entry();
                    boolean disconnect = this.selected.value().dependencies().contains(dependency.key());
                    Component title = disconnect ? UIComponents.REMOVE_DEPENDENCY : UIComponents.ADD_DEPENDENCY;
                    menu.button(title, () -> ClientQuests.updateQuest(this.selected, selected -> {
                        if (disconnect) {
                            selected.dependencies().remove(dependency.key());
                            this.selected.dependencies().remove(dependency);
                            dependency.dependents().remove(this.selected);
                        } else {
                            selected.dependencies().add(dependency.key());
                            this.selected.dependencies().add(dependency);
                            dependency.dependents().add(this.selected);
                        }
                        return NetworkQuestData.builder().dependencies(selected.dependencies());
                    }));
                }
                menu.divider();
                menu.dangerButton(UIComponents.DELETE, () ->
                    widget.delete(quests.get())
                );
            } else {
                menu.button(UIComponents.ADD_QUEST, () -> CreateQuestModal.open((id, name) -> {
                    QuestsWidget quests = this.quests.get();
                    Vector2i local = quests.toLocal(mouseX, mouseY);
                    Quest quest = Quest.of(this.content.group(), name, local.sub(12, 12));
                    ClientQuestNetworking.add(id, quest);
                    NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(content.group(), id));
                }));
            }
        });
        return true;
    }

    @Override
    public boolean onRelease(double mouseX, double mouseY, int button) {
        if (this.dragging != null) {
            Vector2i position = this.dragging.value().display().position(this.content.group());
            setNewPosition(this.dragging, position, DisplayConfig.snapToGrid);
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
            setNewPosition(this.dragging, position, false);
            return TriState.TRUE;
        }
        return TriState.UNDEFINED;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.selected != null) {
            Vector2i position = this.selected.value().display().position(this.content.group());
            boolean handled = true;
            int offset = Screen.hasShiftDown() ? 10 : Screen.hasControlDown() ? 5 : 1;
            switch (keyCode) {
                case InputConstants.KEY_RIGHT -> setNewPosition(this.selected, position.add(offset, 0), false);
                case InputConstants.KEY_LEFT -> setNewPosition(this.selected, position.add(offset * -1, 0), false);
                case InputConstants.KEY_DOWN -> setNewPosition(this.selected, position.add(0, offset), false);
                case InputConstants.KEY_UP -> setNewPosition(this.selected, position.add(0, offset * -1), false);
                case InputConstants.KEY_DELETE -> {
                    QuestWidget widget = this.getSelectedWidget();
                    if (widget != null) widget.delete(this.quests.get());
                }
                default -> handled = false;
            }
            return handled;
        }
        return false;
    }

    private void setNewPosition(ClientQuests.QuestEntry entry, Vector2i position, boolean snapToGrid) {
        ClientQuests.updateQuest(entry, quest -> NetworkQuestData.builder().group(quest, this.content.group(), pos -> {
            if (snapToGrid) {
                pos.x = (position.x() + (Mth.sign(position.x()) * 16)) / 32 * 32;
                pos.y = (position.y() + (Mth.sign(position.y()) * 16)) / 32 * 32;
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

    private void setDetails(ClientQuests.QuestEntry entry, QuestDetailsInitializer.Details details) {
        ClientQuests.updateQuest(entry, quest -> details.build(NetworkQuestData.builder()));
    }

    private void open() {
        if (this.selected == null) return;
        NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(content.group(), this.selected.key()));
        this.selected = null;
        this.lastClick = 0;
    }

    private QuestWidget getSelectedWidget() {
        AtomicReference<QuestWidget> widget = new AtomicReference<>(null);
        this.quests.get().visit(QuestWidget.class, questWidget -> {
            if (questWidget.entry().equals(this.selected)) {
                widget.set(questWidget);
            }
        });
        return widget.get();
    }

    public ClientQuests.QuestEntry getSelected() {
        return selected;
    }
}
