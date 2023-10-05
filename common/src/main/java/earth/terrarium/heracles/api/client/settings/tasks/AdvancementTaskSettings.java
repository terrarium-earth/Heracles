package earth.terrarium.heracles.api.client.settings.tasks;

import com.google.common.collect.Sets;
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.AutocompleteTextSetting;
import earth.terrarium.heracles.api.tasks.defaults.AdvancementTask;
import net.minecraft.Optionull;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class AdvancementTaskSettings implements SettingInitializer<AdvancementTask>, CustomizableQuestElementSettings<AdvancementTask> {

    public static final AdvancementTaskSettings INSTANCE = new AdvancementTaskSettings();

    @Override
    public CreationData create(@Nullable AdvancementTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        ResourceLocation id = Optionull.first(Optionull.mapOrElse(object, AdvancementTask::advancements, List::of));
        settings.put("advancement", AutocompleteTextSetting.ALL_ADVANCEMENT, id);
        return settings;
    }

    @Override
    public AdvancementTask create(String id, @Nullable AdvancementTask object, Data data) {
        return create(object, data, (title, icon) -> new AdvancementTask(
            id,
            title,
            icon,
            data.get("advancement", AutocompleteTextSetting.ALL_ADVANCEMENT)
                .map(Sets::newHashSet)
                .orElse(new HashSet<>())
        ));
    }
}
