package earth.terrarium.heracles.client.ui.quests;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestSettings;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestWidget;
import earth.terrarium.heracles.client.components.quests.QuestsWidget;
import earth.terrarium.heracles.client.components.widgets.context.ContextMenu;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.ui.UIComponents;
import earth.terrarium.heracles.client.ui.modals.EditObjectModal;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

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
        if (widget == null) return false;
        Quest quest = widget.entry().value();
        ContextMenu.open(mouseX, mouseY, 100, menu -> {
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
            menu.divider();
            menu.dangerButton(UIComponents.DELETE, () ->
                widget.delete(quests.get())
            );
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

    public ClientQuests.QuestEntry getSelected() {
        return selected;
    }
}
