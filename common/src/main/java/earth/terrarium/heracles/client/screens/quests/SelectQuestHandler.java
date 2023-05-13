package earth.terrarium.heracles.client.screens.quests;

import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.MouseMode;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2i;

import java.util.function.Consumer;

public class SelectQuestHandler {

    private final Consumer<ClientQuests.QuestEntry> onSelection;

    private long lastClickTime = 0;
    private QuestWidget selectedQuest;

    private Vector2i start = null;
    private Vector2i startOffset = null;

    public SelectQuestHandler(Consumer<ClientQuests.QuestEntry> onSelection) {
        this.onSelection = onSelection;
    }

    public void clickQuest(MouseMode mode, int mouseX, int mouseY, QuestWidget quest) {
        if (selectedQuest == quest) {
            if (Screen.hasShiftDown()) {
                release();
                return;
            } else if (System.currentTimeMillis() - lastClickTime < 250) {
                selectedQuest = null;
                quest.onClicked();
            }
        } else if (mode == MouseMode.SELECT_LINK && selectedQuest != null) {
            if (Screen.hasShiftDown()) {
                quest.quest().dependencies().remove(selectedQuest.id());
                selectedQuest.entry().children().remove(quest.entry());
            } else {
                quest.quest().dependencies().add(selectedQuest.id());
                selectedQuest.entry().children().add(quest.entry());
            }
            ClientQuests.setDirty(quest.id());
            return;
        }
        onSelection.accept(quest.entry());
        selectedQuest = quest;
        lastClickTime = System.currentTimeMillis();
        start = new Vector2i(mouseX, mouseY);
        startOffset = new Vector2i(quest.x(), quest.y());
    }

    public void release() {
        selectedQuest = null;
        start = null;
        startOffset = null;
        onSelection.accept(null);
    }

    public void onDrag(int mouseX, int mouseY) {
        if (selectedQuest != null && start != null && startOffset != null) {
            int newX = mouseX - start.x() + startOffset.x();
            int newY = mouseY - start.y() + startOffset.y();
            selectedQuest.position().x = newX;
            selectedQuest.position().y = newY;
            ClientQuests.setDirty(selectedQuest.id());
        }
    }

    public QuestWidget selectedQuest() {
        return selectedQuest;
    }

}
