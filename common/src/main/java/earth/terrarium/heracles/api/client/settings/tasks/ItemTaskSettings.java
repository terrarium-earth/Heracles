package earth.terrarium.heracles.api.client.settings.tasks;

import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public class ItemTaskSettings implements SettingInitializer<GatherItemTask> {

    public static final ItemTaskSettings INSTANCE = new ItemTaskSettings();

    @Override
    public CreationData create(@Nullable GatherItemTask object) {
        CreationData settings = new CreationData();
        settings.put("item", RegistryValueSetting.ITEM, Optionull.map(object, GatherItemTask::item));
        settings.put("amount", IntSetting.ONE, Optionull.map(object, GatherItemTask::target));
        return settings;
    }

    @Override
    public GatherItemTask create(String id, GatherItemTask object, Data data) {
        RegistryValue<Item> item = data.get("item", RegistryValueSetting.ITEM).orElse(object.item());
        NbtPredicate old = Optionull.mapOrDefault(object, GatherItemTask::nbt, NbtPredicate.ANY);
        return new GatherItemTask(id, item, old, data.get("amount", IntSetting.ONE).orElse(1));
    }
}
