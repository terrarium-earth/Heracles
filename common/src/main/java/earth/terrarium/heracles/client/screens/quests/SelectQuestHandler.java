package earth.terrarium.heracles.client.screens.quests;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.mousemode.MouseMode;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.function.Consumer;

public class SelectQuestHandler {

    private final String group;
    private final Consumer<ClientQuests.QuestEntry> onSelection;

    private long lastClickTime = 0;
    private QuestWidget selectedQuest;
    private ArrayList<QuestWidget> selectedQuests = new ArrayList<>();

    private Vector2i start = null;
    private Vector2i startOffset = null;

    public SelectQuestHandler(String group, Consumer<ClientQuests.QuestEntry> onSelection) {
        this.group = group;
        this.onSelection = onSelection;
    }

    public void clickQuest(MouseMode mode, int mouseX, int mouseY, QuestWidget quest) {
        if (selectedQuest == quest) {
            if (Screen.hasShiftDown()) {
                release();
                return;
            } else if (System.currentTimeMillis() - lastClickTime < 500) {
                selectedQuest = null;
                NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(
                    this.group, quest.id(), Minecraft.getInstance().screen instanceof QuestsEditScreen
                ));
            }
        } else if (mode == MouseMode.SELECT_LINK && selectedQuest != null) {
            ClientQuests.updateQuest(quest.entry(), value -> {
                if (Screen.hasShiftDown()) {
                    quest.quest().dependencies().remove(selectedQuest.id());
                    selectedQuest.entry().dependents().remove(quest.entry());
                    quest.entry().dependencies().remove(quest.entry());
                } else {
                    if (!quest.entry().dependents().contains(selectedQuest.entry())) {
                        if (quest.quest().dependencies().add(selectedQuest.id())) {
                            selectedQuest.entry().dependents().add(quest.entry());
                            quest.entry().dependencies().add(selectedQuest.entry());
                        }
                    }
                }
                return NetworkQuestData.builder().dependencies(quest.entry().value().dependencies());
            });
            return;
        }

        // If we select a quest without holding control, clear our selection entirely
        if (!Screen.hasControlDown()) {
            selectedQuests = new ArrayList<>();
            selectedQuest = null;
        }

        // If we are holding control, and have already selected a previous quest,
        // Add the previous quest to the selection and set the top selection to the new one
        if (Screen.hasControlDown() && selectedQuest != null) {
            selectedQuests.add(selectedQuest);
        }

        onSelection.accept(quest.entry());
        selectedQuest = quest;

        // Make sure our top selected quest isn't grouped in with the rest of the selected quests
        // Otherwise it'll move when it shouldn't. This can happen if the user selects quest A,
        // adds quest B, then adds quest A again. Quest A will then be in the selectedQuests unless we remove it here.
        selectedQuests.remove(quest);

        lastClickTime = System.currentTimeMillis();
        start = new Vector2i(mouseX, mouseY);
        startOffset = new Vector2i(quest.x(), quest.y());
    }

    public void release() {
        selectedQuests = new ArrayList<>();
        selectedQuest = null;
        start = null;
        startOffset = null;
        onSelection.accept(null);
    }

    public void onDrag(int mouseX, int mouseY) {
        if (selectedQuest != null && start != null && startOffset != null) {
            int newX = mouseX - start.x() + startOffset.x();
            int newY = mouseY - start.y() + startOffset.y();

            moveAllSelected(newX, newY);
        }
    }

    public boolean onKeyPress(int key) {
        int x = 0;
        int y = 0;
        switch (key) {
            case InputConstants.KEY_UP -> y = -1;
            case InputConstants.KEY_DOWN -> y = 1;
            case InputConstants.KEY_LEFT -> x = -1;
            case InputConstants.KEY_RIGHT -> x = 1;
        }

        if (Screen.hasShiftDown()) {
            x *= 10;
            y *= 10;
        } else if (Screen.hasControlDown()) {
            x *= 5;
            y *= 5;
        }

        if (x == 0 && y == 0) return false;
        if (selectedQuest == null) return false;

        int newX = selectedQuest.x() + x;
        int newY = selectedQuest.y() + y;

        moveAllSelected(newX, newY);

        return true;
    }

    /**
     * Moves the top quest ({@link #selectedQuest}) to the newX and newY given.
     * <p>
     * The selected quests ({@link #selectedQuests}) get moved relative to the top quest.
     */
    private void moveAllSelected(int newX, int newY) {
        // Move the selected quests which aren't the top selected quest first, so that the anchor point doesn't move
        selectedQuests.forEach((w) -> {
            Vector2i offset = new Vector2i(w.position()).sub(selectedQuest.position());
            ClientQuests.updateQuest(w.entry(), quest ->
                    NetworkQuestData.builder().group(quest, w.group(), pos -> {
                        pos.x = newX + offset.x;
                        pos.y = newY + offset.y;
                        return pos;
                    }),
                false
            );
        });

        // Then move the top/main selected quest
        ClientQuests.updateQuest(selectedQuest.entry(), quest ->
                NetworkQuestData.builder().group(quest, selectedQuest.group(), pos -> {
                    pos.x = newX;
                    pos.y = newY;
                    return pos;
                }),
            false
        );
    }

    /**
     * Returns the top selected quest of this handler
     */
    public QuestWidget selectedQuest() {
        return selectedQuest;
    }


    /**
     * Checks if the widget is in {@link #selectedQuests} OR if the widget equals the {@link #selectedQuest()}
     * @return true if the widget meets either condition
     */
    public boolean shouldRenderAsSelected(QuestWidget widget) {
        return (widget == selectedQuest) || selectedQuests.contains(widget);
    }
}
