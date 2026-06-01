package earth.terrarium.heracles.client.ui.quests;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestSettings;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestWidget;
import earth.terrarium.heracles.client.components.quests.QuestsWidget;
import earth.terrarium.heracles.client.handlers.ClientQuestNetworking;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.ui.modals.CreateQuestModal;
import earth.terrarium.heracles.client.ui.modals.EditObjectModal;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.ServerboundResetQuestProgressPacket;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.olympus.client.ui.context.ContextMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.function.Supplier;

public class EditActionHandler implements QuestActionHandler {

    private static final ResourceLocation QUEST = Heracles.id("quest");

    private final Supplier<QuestsWidget> quests;
    private QuestsContent content;

    // A bit messy storing both of these, but eh
    private QuestWidget topSelectedWidget;

    private ArrayList<QuestWidget> allSelected = new ArrayList<>();

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
            this.topSelectedWidget = null;
            this.allSelected = new ArrayList<>();
            return false;
        }
        ClientQuests.QuestEntry entry = widget.entry();
        if (entry.equals(getSelected()) && System.currentTimeMillis() - this.lastClick < 500) {
            this.open();
        } else {

            // If we select a quest without holding control, clear our selection entirely
            if (!Screen.hasControlDown()) {
                allSelected = new ArrayList<>();
                topSelectedWidget = null;
            }

            // If we are holding control, and have already selected a previous quest,
            // Add the previous quest to the selection and set the top selection to the new one
            if (Screen.hasControlDown() && topSelectedWidget != null) {
                allSelected.add(topSelectedWidget);
            }

            this.topSelectedWidget = widget;

            // Make sure our top selected quest isn't grouped in with the rest of the selected quests
            // Otherwise it'll move when it shouldn't. This can happen if the user selects quest A,
            // adds quest B, then adds quest A again. Quest A will then be in the selectedQuests unless we remove it here.
            allSelected.remove(widget);

            this.lastClick = System.currentTimeMillis();
            this.start = new Vector2i((int) mouseX, (int) mouseY);
            this.startOffset = widget.position();
            this.dragging = entry;
            this.quests.get().select(questWidget -> questWidget == widget || allSelected.contains(questWidget));
        }

        return true;
    }

    public void deferContextMenuOpening(Runnable runnable) {
        Minecraft.getInstance().tell(runnable);
    }

    @Override
    public boolean onRightClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
        ContextMenu.open(mouseX, mouseY, menu -> {
            if (widget != null) {
                Quest quest = widget.entry().value();
                menu.button(Component.translatable("gui.heracles.edit.details"), () -> deferContextMenuOpening(() -> EditObjectModal.open(
                    QuestDetailsInitializer.INSTANCE, QUEST, Component.translatable("gui.heracles.edit.details"), null,
                    new QuestDetailsInitializer.Details(quest), data -> setDetails(widget.entry(), data)
                )));
                menu.button(Component.translatable("gui.heracles.edit.settings"), () -> deferContextMenuOpening(() -> {
                    EditObjectModal.open(
                        QuestSettingsInitializer.INSTANCE, QUEST, Component.translatable("gui.heracles.edit.settings"), null,
                        quest.settings(), data -> setSettings(widget.entry(), data)
                    );
                }));
                menu.divider();
                menu.button(Component.translatable("gui.heracles.quests.snap_to_grid"), () ->
                    setNewPosition(widget.entry(), widget.position(), true)
                );
                if (getSelected() != null && getSelected() != widget.entry()) {
                    ClientQuests.QuestEntry dependency = widget.entry();
                    boolean disconnect = getSelected().value().dependencies().contains(dependency.key());
                    Component title = disconnect ? Component.translatable("gui.heracles.dependency_remove") : Component.translatable("gui.heracles.dependency_add");
                    menu.button(title, () -> ClientQuests.updateQuest(getSelected(), selected -> {
                        if (disconnect) {
                            selected.dependencies().remove(dependency.key());
                            getSelected().dependencies().remove(dependency);
                            dependency.dependents().remove(getSelected());
                        } else {
                            selected.dependencies().add(dependency.key());
                            getSelected().dependencies().add(dependency);
                            dependency.dependents().add(getSelected());
                        }
                        return NetworkQuestData.builder().dependencies(selected.dependencies());
                    }));
                }
                menu.button(Component.literal("Reset Progress"), () -> {
                    NetworkHandler.CHANNEL.sendToServer(new ServerboundResetQuestProgressPacket(widget.entry().key()));
                });
                menu.divider();
                menu.dangerButton(Component.translatable("gui.heracles.delete"), () ->
                    widget.delete(quests.get())
                );
            } else {
                menu.button(Component.translatable("gui.heracles.add_quest"), () -> deferContextMenuOpening(() ->  CreateQuestModal.open((id, name) -> {
                    QuestsWidget quests = this.quests.get();
                    Vector2i local = quests.toLocal(mouseX, mouseY);
                    Quest quest = Quest.of(this.content.group(), name, local.sub(12, 12));
                    ClientQuestNetworking.add(id, quest);
                    NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(content.group(), id));
                })));
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
            setNewPosition(this.dragging, position, DisplayConfig.snapToGrid);
            return TriState.TRUE;
        }
        return TriState.UNDEFINED;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (getSelected() != null) {
            Vector2i position = new Vector2i(topSelectedWidget.position());
            boolean handled = true;
            int offset = Screen.hasShiftDown() ? 10 : Screen.hasControlDown() ? 5 : 1;
            switch (keyCode) {
                case InputConstants.KEY_RIGHT -> setNewPosition(getSelected(), position.add(offset, 0), false);
                case InputConstants.KEY_LEFT -> setNewPosition(getSelected(), position.add(offset * -1, 0), false);
                case InputConstants.KEY_DOWN -> setNewPosition(getSelected(), position.add(0, offset), false);
                case InputConstants.KEY_UP -> setNewPosition(getSelected(), position.add(0, offset * -1), false);
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

        int newX;
        int newY;

        if (snapToGrid) {
            newX = (int) (Math.floor((position.x() - 2f) / 27) * 27 + 2 + 17);
            newY = (int) (Math.floor((position.y() - 2f) / 27) * 27 + 2 + 13);
        } else {
            newX = position.x();
            newY = position.y();
        }

        // Move the selected quests which aren't the top selected quest first, so that the anchor point doesn't move
        allSelected.forEach((w) -> {
            Vector2i o = new Vector2i(w.position()).sub(topSelectedWidget.position());
            ClientQuests.updateQuest(w.entry(), quest ->
                    NetworkQuestData.builder().group(quest, this.content.group(), pos -> {
                        pos.x = newX + o.x;
                        pos.y = newY + o.y;
                        return pos;
                    })
            );
        });

        // Then move the top/main selected quest
        ClientQuests.updateQuest(entry, quest -> NetworkQuestData.builder().group(quest, this.content.group(), pos -> {
            pos.x = newX;
            pos.y = newY;
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
        if (getSelected() == null) return;
        NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(content.group(), getSelected().key()));
        this.topSelectedWidget = null;
        this.allSelected = new ArrayList<>();
        this.lastClick = 0;
    }

    private QuestWidget getSelectedWidget() {
        return topSelectedWidget;
    }

    public ClientQuests.QuestEntry getSelected() {
        return (topSelectedWidget != null) ? topSelectedWidget.entry() : null;
    }

    public void setContent(QuestsContent content) {
        this.content = content;
    }
}
