package earth.terrarium.heracles.api.client.settings.tasks;

import com.google.common.collect.Sets;
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.AutocompleteTextSetting;
import earth.terrarium.heracles.api.tasks.defaults.RecipeTask;
import net.minecraft.Optionull;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class RecipeTaskSettings implements SettingInitializer<RecipeTask>, CustomizableQuestElementSettings<RecipeTask> {

    public static final RecipeTaskSettings INSTANCE = new RecipeTaskSettings();

    @Override
    public CreationData create(@Nullable RecipeTask object) {
        CreationData settings = CustomizableQuestElementSettings.super.create(object);
        ResourceLocation id = Optionull.first(Optionull.mapOrElse(object, RecipeTask::recipes, List::of));
        settings.put("recipe", AutocompleteTextSetting.ALL_RECIPES, id);
        return settings;
    }

    @Override
    public RecipeTask create(String id, @Nullable RecipeTask object, Data data) {
        return create(object, data, (title, icon) -> new RecipeTask(
            id,
            title,
            icon,
            data.get("recipe", AutocompleteTextSetting.ALL_RECIPES)
                .map(Sets::newHashSet)
                .orElse(new HashSet<>())
        ));
    }
}
