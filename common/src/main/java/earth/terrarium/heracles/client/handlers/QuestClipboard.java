package earth.terrarium.heracles.client.handlers;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.screens.quests.QuestsEditScreen;
import earth.terrarium.heracles.client.screens.quests.QuestsWidget;
import earth.terrarium.heracles.client.screens.quests.SelectQuestWidget;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.utils.MouseClick;
import earth.terrarium.heracles.client.widgets.modals.TextInputModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2i;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class QuestClipboard {

    public static final QuestClipboard INSTANCE = new QuestClipboard();

    private Quest quest;
    private String key;

    public boolean action(int keyCode, SelectQuestWidget widget) {
        if (Screen.isCut(keyCode)) {
            action(widget.widget(), widget.entry(), QuestAction.CUT);
            return true;
        } else if (Screen.isCopy(keyCode)) {
            action(widget.widget(), widget.entry(), QuestAction.COPY);
            return true;
        } else if (Screen.isPaste(keyCode)) {
            if (ClientUtils.screen() instanceof QuestsEditScreen screen) {
                screen.questModal().setData(ClientUtils.getMousePos());
                paste(widget.widget(), screen.questModal(), entry -> widget.widget().addQuest(entry));
                return true;
            }
        }
        return false;
    }

    public void action(QuestsWidget widget, ClientQuests.QuestEntry entry, QuestAction action) {
        this.quest = null;
        this.key = null;
        switch (action) {
            case CUT -> {
                this.quest = entry.value();
                this.key = entry.key();
                ClientQuests.removeQuest(entry);
                widget.removeQuest(entry);
            }
            case COPY -> this.quest = entry.value();
            default -> throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    public void paste(QuestsWidget widget, TextInputModal<MouseClick> modal, Consumer<ClientQuests.QuestEntry> callback) {
        if (this.quest != null) {
            if (this.key != null) {
                if (ClientQuests.get(this.key).isPresent()) {
                    this.key = null;
                }
            }
            if (this.key != null) {
                create(this.key, widget, modal.getData(), callback);
                this.key = null;
                this.quest = null;
            } else {
                modal.setVisible(true);
                modal.setCallback((click, id) -> {
                    create(id, widget, click, callback);
                    this.quest = null;
                });
            }
        }
    }

    private void create(String id, QuestsWidget widget, MouseClick mouse, Consumer<ClientQuests.QuestEntry> callback) {
        if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
            MouseClick local = widget.getLocal(mouse);
            String group = screen.getMenu().group();
            Quest newQuest = new Quest(
                new QuestDisplay(
                    quest.display().icon(),
                    quest.display().iconBackground(),
                    quest.display().title(),
                    quest.display().subtitle(),
                    quest.display().description(),
                    new Vector2i((int) local.x() - 12, (int) local.y() - 12),
                    group
                ),
                quest.settings(),
                Set.copyOf(quest.dependencies()),
                Map.copyOf(quest.tasks()),
                Map.copyOf(quest.rewards())
            );
            callback.accept(ClientQuests.addQuest(id, newQuest));
        }
    }

    public enum QuestAction {
        COPY,
        CUT
    }

}
