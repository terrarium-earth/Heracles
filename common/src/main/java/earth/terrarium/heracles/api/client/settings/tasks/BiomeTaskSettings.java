package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.tasks.defaults.BiomeTask;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

public class BiomeTaskSettings implements SettingInitializer<BiomeTask> {

    public static final BiomeTaskSettings INSTANCE = new BiomeTaskSettings();

    @Override
    public CreationData create(@Nullable BiomeTask object) {
        CreationData settings = new CreationData();
        settings.put("biome", RegistryValueSetting.BIOME, Optionull.map(object, BiomeTask::biomes));
        return settings;
    }

    @Override
    public BiomeTask create(String id, @Nullable BiomeTask object, Data data) {
        return new BiomeTask(
            id,
            data.get("biome", RegistryValueSetting.BIOME).orElse(Optionull.mapOrDefault(object, BiomeTask::biomes, null))
        );
    }
}
