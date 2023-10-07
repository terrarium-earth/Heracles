package earth.terrarium.heracles.api.client.settings.rewards;

import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.RegistrySetting;
import earth.terrarium.heracles.api.rewards.defaults.ItemReward;
import net.minecraft.Optionull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ItemRewardSettings implements SettingInitializer<ItemReward>, CustomizableQuestElementSettings<ItemReward> {

    public static final ItemRewardSettings INSTANCE = new ItemRewardSettings();

    @Override
    public CreationData create(@Nullable ItemReward object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        settings.put("item", RegistrySetting.ITEM, getDefaultItem(object));
        settings.put("count", IntSetting.ONE, getDefaultCount(object));
        return settings;
    }

    @Override
    public ItemReward create(String id, @Nullable ItemReward object, Data data) {
        ItemStack stack = new ItemStack(
            data.get("item", RegistrySetting.ITEM).orElse(getDefaultItem(object)),
            data.get("count", IntSetting.ONE).orElse(getDefaultCount(object))
        );
        if (object != null) {
            stack.setTag(object.stack().getTag());
        }

        return create(object, data, (title, icon) -> new ItemReward(
            id,
            title,
            icon,
            stack
        ));
    }

    private static int getDefaultCount(@Nullable ItemReward object) {
        return Optionull.mapOrDefault(object, reward -> reward.stack().getCount(), 1);
    }

    private static Item getDefaultItem(@Nullable ItemReward object) {
        return Optionull.mapOrDefault(object, reward -> reward.stack().getItem(), Items.AIR);
    }
}

