package earth.terrarium.heracles.api.client.settings;

import earth.terrarium.heracles.api.client.settings.rewards.*;
import earth.terrarium.heracles.api.client.settings.tasks.*;
import earth.terrarium.heracles.api.rewards.defaults.*;
import earth.terrarium.heracles.api.tasks.defaults.*;

import java.util.IdentityHashMap;
import java.util.Map;

public final class Settings {

    private static final Map<SettingsProvider<?>, SettingInitializer<?>> FACTORIES = new IdentityHashMap<>();

    public static <T> void register(SettingsProvider<T> type, SettingInitializer<T> factory) {
        FACTORIES.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T> SettingInitializer<T> getFactory(SettingsProvider<T> type) {
        return hasFactory(type) ? (SettingInitializer<T>) FACTORIES.get(type) : null;
    }

    public static boolean hasFactory(SettingsProvider<?> type) {
        return FACTORIES.containsKey(type);
    }

    static {
        register(ChangedDimensionTask.TYPE, DimensionTaskSettings.INSTANCE);
        register(KillEntityQuestTask.TYPE, KillEntityTaskSettings.INSTANCE);
        register(GatherItemTask.TYPE, ItemTaskSettings.INSTANCE);
        register(RecipeTask.TYPE, RecipeTaskSettings.INSTANCE);
        register(AdvancementTask.TYPE, AdvancementTaskSettings.INSTANCE);
        register(StructureTask.TYPE, StructureTaskSettings.INSTANCE);
        register(BiomeTask.TYPE, BiomeTaskSettings.INSTANCE);
        register(BlockInteractTask.TYPE, BlockInteractTaskSettings.INSTANCE);
        register(ItemInteractTask.TYPE, ItemInteractTaskSettings.INSTANCE);
        register(EntityInteractTask.TYPE, EntityInteractTaskSettings.INSTANCE);
        register(DummyTask.TYPE, DummyTaskSettings.INSTANCE);
        register(XpTask.TYPE, XpTaskSettings.INSTANCE);
        register(StatTask.TYPE, StatTaskSettings.INSTANCE);
        register(CheckTask.TYPE, CheckTaskSettings.INSTANCE);

        register(LootTableReward.TYPE, LootRewardSettings.INSTANCE);
        register(ItemReward.TYPE, ItemRewardSettings.INSTANCE);
        register(XpQuestReward.TYPE, XpRewardSettings.INSTANCE);
        register(CommandReward.TYPE, CommandRewardSettings.INSTANCE);
        register(SelectableReward.TYPE, SelectableRewardSettings.INSTANCE);
    }
}
