package earth.terrarium.heracles.api.client.settings.tasks;

import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.RegistrySetting;
import earth.terrarium.heracles.api.tasks.defaults.StatTask;
import net.minecraft.Optionull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import org.jetbrains.annotations.Nullable;

public class StatTaskSettings implements SettingInitializer<StatTask> {

    public static final StatTaskSettings INSTANCE = new StatTaskSettings();

    @Override
    public CreationData create(@Nullable StatTask object) {
        CreationData settings = new CreationData();
        settings.put("stat", RegistrySetting.STAT, getDefaultStat(object));
        settings.put("target", IntSetting.ONE, getDefaultCount(object));
        return settings;
    }

    @Override
    public StatTask create(String id, StatTask object, Data data) {
        return new StatTask(
            id,
            data.get("stat", RegistrySetting.STAT).orElse(getDefaultStat(object)),
            data.get("target", IntSetting.ONE).orElse(getDefaultCount(object))
        );
    }

    private static ResourceLocation getDefaultStat(StatTask object) {
        return Optionull.mapOrDefault(object, StatTask::stat, Stats.JUMP);
    }

    private static int getDefaultCount(StatTask object) {
        return Optionull.mapOrDefault(object, StatTask::target, 1);
    }
}
