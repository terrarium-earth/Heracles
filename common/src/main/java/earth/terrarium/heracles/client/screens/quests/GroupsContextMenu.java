package earth.terrarium.heracles.client.screens.quests;

import com.teamresourceful.resourcefullib.client.components.context.ContextMenu;

public class GroupsContextMenu extends ContextMenu {

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.visible && this.active) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }
}
