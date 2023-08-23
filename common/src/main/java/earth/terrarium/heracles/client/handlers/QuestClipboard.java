package earth.terrarium.heracles.client.handlers;

import earth.terrarium.heracles.api.quests.GroupDisplay;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.screens.quests.QuestsEditScreen;
import earth.terrarium.heracles.client.screens.quests.QuestsWidget;
import earth.terrarium.heracles.client.screens.quests.SelectQuestWidget;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.utils.MouseClick;
import earth.terrarium.heracles.client.widgets.modals.TextInputModal;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.Util;
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
        if (Screen.isCut(keyCode) && widget.entry() != null) {
            action(widget.widget(), widget.entry(), QuestAction.CUT);
            return true;
        } else if (Screen.isCopy(keyCode) && widget.entry() != null) {
            action(widget.widget(), widget.entry(), QuestAction.COPY);
            return true;
        } else if (Screen.isPaste(keyCode)) {
            if (ClientUtils.screen() instanceof QuestsEditScreen screen) {
                screen.questModal().setData(ClientUtils.getMousePos());
                paste(widget.widget(), screen.questModal(), entry -> widget.widget().addQuest(entry));
                return true;
            }
        } else if (isSpecialPaste(keyCode)) {
            if (Minecraft.getInstance().screen instanceof QuestsEditScreen screen) {
                MouseClick pos = widget.widget().getLocal(ClientUtils.getMousePos());
                String group = screen.getGroup();
                ClientQuests.get(this.key).ifPresent(entry -> {
                    if (entry.value().display().groups().containsKey(group)) return;
                    ClientQuests.updateQuest(entry, quest -> {
                        quest.display().groups().put(group, new GroupDisplay(group, new Vector2i((int) pos.x() - 12, (int) pos.y() - 12)));
                        return NetworkQuestData.builder().groups(quest.display().groups());
                    });
                    widget.widget().addQuest(entry);
                });
                return true;
            }
        }
        return false;
    }

    private static boolean isSpecialPaste(int keycode) {
        return keycode == 86 && Screen.hasControlDown() && Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void action(QuestsWidget widget, ClientQuests.QuestEntry entry, QuestAction action) {
        this.quest = null;
        this.key = null;
        switch (action) {
            case CUT -> {
                this.quest = entry.value();
                this.key = entry.key();
                if (this.quest.display().groups().size() == 1) {
                    ClientQuestNetworking.remove(entry.key());
                } else {
                    ClientQuests.updateQuest(entry, quest -> {
                        quest.display().groups().remove(widget.group());
                        return NetworkQuestData.builder().groups(quest.display().groups());
                    });
                }
                widget.removeQuest(entry);
            }
            case COPY -> {
                this.quest = entry.value();
                this.key = entry.key();
            }
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
            String group = screen.getGroup();
            Quest newQuest = new Quest(
                new QuestDisplay(
                    quest.display().icon(),
                    quest.display().iconBackground(),
                    quest.display().title(),
                    quest.display().subtitle(),
                    quest.display().description(),
                    Util.make(quest.display().groups(), groups ->
                        groups.put(group, new GroupDisplay(group, new Vector2i((int) local.x() - 12, (int) local.y() - 12)))
                    )
                ),
                quest.settings(),
                Set.copyOf(quest.dependencies()),
                Map.copyOf(quest.tasks()),
                Map.copyOf(quest.rewards())
            );
            callback.accept(ClientQuestNetworking.add(id, newQuest));
        }
    }

    public enum QuestAction {
        COPY,
        CUT
    }

}
