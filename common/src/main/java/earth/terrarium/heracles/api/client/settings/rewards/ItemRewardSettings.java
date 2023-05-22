package earth.terrarium.heracles.api.client.settings.rewards;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.RegistrySetting;
import earth.terrarium.heracles.api.rewards.defaults.ItemReward;
import net.minecraft.Optionull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ItemRewardSettings implements SettingInitializer<ItemReward> {

    public static final ItemRewardSettings INSTANCE = new ItemRewardSettings();

    @Override
    public CreationData create(@Nullable ItemReward object) {
        CreationData settings = new CreationData();
        settings.put("item", RegistrySetting.ITEM, Optionull.map(object, reward -> reward.stack().getItem()));
        settings.put("count", IntSetting.ONE, Optionull.map(object, reward -> reward.stack().getCount()));
        return settings;
    }

    @Override
    public ItemReward create(String id, @Nullable ItemReward object, Data data) {
        ItemStack stack = new ItemStack(
            data.get("item", RegistrySetting.ITEM).orElse(Optionull.mapOrDefault(object, reward -> reward.stack().getItem(), Items.AIR)),
            data.get("count", IntSetting.ONE).orElse(Optionull.mapOrDefault(object, reward -> reward.stack().getCount(), 1))
        );
        if (object != null) {
            stack.setTag(object.stack().getTag());
        }

        return new ItemReward(id, stack);
    }
}

