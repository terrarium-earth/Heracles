package earth.terrarium.heracles.api.client.settings.base;

import com.mojang.datafixers.util.Either;
import earth.terrarium.heracles.api.client.settings.Setting;
import earth.terrarium.heracles.client.components.widgets.item.ItemButton;
import earth.terrarium.heracles.common.utils.ItemValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ItemSetting(boolean tagsAllowed) implements Setting<Either<ItemStack, TagKey<Item>>, ItemButton> {
    public static final ItemSetting INSTANCE = new ItemSetting(true);
    public static final ItemSetting NO_TAGS = new ItemSetting(false);

    @Override
    public ItemButton createWidget(ItemButton old, int width, Either<ItemStack, TagKey<Item>> value) {
        return new ItemButton(old, new ItemValue(value), width, 24, tagsAllowed);
    }

    @Override
    public Either<ItemStack, TagKey<Item>> getValue(ItemButton widget) {
        return widget.reference().get().item();
    }
}
