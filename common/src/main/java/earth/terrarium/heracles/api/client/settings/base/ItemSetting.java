package earth.terrarium.heracles.api.client.settings.base;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.widgets.buttons.ItemButton;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ItemSetting(boolean tagsAllowed) implements Setting<Either<ItemStack, TagKey<Item>>, ItemButton> {
    public static final ItemSetting INSTANCE = new ItemSetting(true);
    public static final ItemSetting NO_TAGS = new ItemSetting(false);

    @Override
    public ItemButton createWidget(int width, Either<ItemStack, TagKey<Item>> value) {
        return new ItemButton(0, 0, width, 11, tagsAllowed, value);
    }

    @Override
    public Either<ItemStack, TagKey<Item>> getValue(ItemButton widget) {
        return widget.value();
    }
}
