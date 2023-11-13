package earth.terrarium.heracles.client.widgets.buttons;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.client.screens.quest.QuestEditScreen;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemButton extends Button implements ThemedButton {

    private final boolean tagsAllowed;
    private Either<ItemStack, TagKey<Item>> value;

    public ItemButton(int x, int y, int width, int height, boolean tagsAllowed, Either<ItemStack, TagKey<Item>> value) {
        super(x, y, width, height, CommonComponents.EMPTY, b -> {}, DEFAULT_NARRATION);
        this.tagsAllowed = tagsAllowed;
        this.value = value;
    }

    @Override
    public void onPress() {
        if (Minecraft.getInstance().screen instanceof QuestEditScreen screen) {
            screen.itemModal().setCurrent(this.value);
            screen.itemModal().setTagsAllowed(this.tagsAllowed);
            screen.itemModal().setVisible(true);
            screen.itemModal().setCallback(item -> {
                this.value = item;
                screen.itemModal().setVisible(false);
                screen.itemModal().setCurrent(null);
            });
        }
    }

    @Override
    public @NotNull Component getMessage() {
        return value().map(ItemStack::getHoverName, RegistryValue::getDisplayName);
    }

    public Either<ItemStack, TagKey<Item>> value() {
        return this.value;
    }
}
