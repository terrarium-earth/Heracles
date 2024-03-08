package earth.terrarium.heracles.client.components.quests;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import org.jetbrains.annotations.Nullable;

public interface QuestActionHandler {

    default boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default boolean onClick(double mouseX, double mouseY, int button, @Nullable QuestWidget widget) {
        if (button == InputConstants.MOUSE_BUTTON_LEFT) {
            return this.onLeftClick(mouseX, mouseY, widget);
        } else if (button == InputConstants.MOUSE_BUTTON_RIGHT) {
            return this.onRightClick(mouseX, mouseY, widget);
        }
        return false;
    }

    default boolean onLeftClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
        return false;
    }

    default boolean onRightClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
        return false;
    }

    default boolean onRelease(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Called when the mouse is dragged.
     * @return {@link TriState#TRUE} if the drag was handled, {@link TriState#FALSE} if it was not, and {@link TriState#UNDEFINED} if the drag was not handled and the widget should handle it.
     */
    default TriState onDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return TriState.UNDEFINED;
    }
}
