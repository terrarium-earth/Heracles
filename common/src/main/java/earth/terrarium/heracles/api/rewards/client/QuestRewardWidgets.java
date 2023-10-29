package earth.terrarium.heracles.api.rewards.client;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.client.defaults.*;
import earth.terrarium.heracles.api.rewards.defaults.*;
import net.minecraft.Optionull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class QuestRewardWidgets {

    private static final Map<QuestRewardType<?>, QuestRewardWidgetFactory<?>> FACTORIES = new IdentityHashMap<>();

    public static <T extends QuestReward<T>> void register(QuestRewardType<T> type, QuestRewardWidgetFactory<T> factory) {
        FACTORIES.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends QuestReward<T>> QuestRewardWidgetFactory<T> getFactory(QuestRewardType<T> type) {
        if (!FACTORIES.containsKey(type)) {
            return null;
        }
        return (QuestRewardWidgetFactory<T>) FACTORIES.get(type);
    }

    @Nullable
    public static DisplayWidget create(QuestReward<?> task) {
        return Optionull.map(getFactory(task.type()), factory -> factory.createAndCast(task));
    }

    static {
        register(XpQuestReward.TYPE, XpRewardWidget::of);
        register(ItemReward.TYPE, ItemRewardWidget::of);
        register(LootTableReward.TYPE, LootTableRewardWidget::of);
        register(SelectableReward.TYPE, SelectableRewardWidget::of);
        register(CommandReward.TYPE, CommandRewardWidget::of);
    }
}
