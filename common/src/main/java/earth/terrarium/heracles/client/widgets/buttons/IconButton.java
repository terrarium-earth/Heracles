package earth.terrarium.heracles.client.widgets.buttons;

import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.client.screens.quest.QuestEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class IconButton extends Button {

    private ItemQuestIcon value;

    public IconButton(int x, int y, int width, int height, ItemQuestIcon value) {
        super(x, y, width, height, CommonComponents.EMPTY, b -> {}, DEFAULT_NARRATION);
        this.value = value;
    }

    @Override
    public void onPress() {
        if (Minecraft.getInstance().screen instanceof QuestEditScreen screen) {
            screen.iconModal().setVisible(true);
            screen.iconModal().setCallback(item -> {
                this.value = new ItemQuestIcon(item);
                screen.iconModal().setVisible(false);
            });
        }
    }

    @Override
    public @NotNull Component getMessage() {
        return this.value.item() == Items.AIR ? Component.literal("") : this.value.item().getDescription();
    }

    public ItemQuestIcon value() {
        return this.value;
    }
}
