package earth.terrarium.heracles.api.tasks.client;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.client.defaults.*;
import earth.terrarium.heracles.api.tasks.defaults.*;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.Optionull;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class QuestTaskWidgets {

    private static final Map<QuestTaskType<?>, QuestTaskWidgetFactory<?, ?, ?>> FACTORIES = new IdentityHashMap<>();

    public static <I, S extends Tag, T extends QuestTask<I, S, T>> void registerSimple(QuestTaskType<T> type, QuestTaskWidgetSimpleFactory<I, S, T> factory) {
        FACTORIES.put(type, factory);
    }

    public static <I, S extends Tag, T extends QuestTask<I, S, T>> void register(QuestTaskType<T> type, QuestTaskWidgetBasicFactory<I, S, T> factory) {
        FACTORIES.put(type, factory);
    }

    public static <I, S extends Tag, T extends QuestTask<I, S, T>> void register(QuestTaskType<T> type, QuestTaskWidgetFactory<I, S, T> factory) {
        FACTORIES.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public static <I, S extends Tag, T extends QuestTask<I, S, T>> QuestTaskWidgetFactory<I, S, T> getFactory(QuestTaskType<T> type) {
        if (!FACTORIES.containsKey(type)) {
            return null;
        }
        return (QuestTaskWidgetFactory<I, S, T>) FACTORIES.get(type);
    }

    @Nullable
    public static <T extends Tag> DisplayWidget create(String quest, QuestTask<?, T, ?> task, TaskProgress<T> progress, ModUtils.QuestStatus status) {
        return Optionull.map(getFactory(task.type()), factory -> factory.createAndCast(quest, task, progress, status));
    }

    static {
        registerSimple(KillEntityQuestTask.TYPE, KillEntityTaskWidget::new);
        register(GatherItemTask.TYPE, ItemTaskWidget::new);
        registerSimple(AdvancementTask.TYPE, AdvancementTaskWidget::new);
        registerSimple(RecipeTask.TYPE, RecipeTaskWidget::new);
        registerSimple(StructureTask.TYPE, StructureTaskWidget::new);
        registerSimple(BiomeTask.TYPE, BiomeTaskWidget::new);
        registerSimple(BlockInteractTask.TYPE, BlockInteractTaskWidget::new);
        registerSimple(ItemInteractTask.TYPE, ItemInteractTaskWidget::new);
        registerSimple(ChangedDimensionTask.TYPE, DimensionTaskWidget::new);
        register(CompositeTask.TYPE, CompositeTaskWidget::new);
        register(CheckTask.TYPE, CheckTaskWidget::new);
        registerSimple(DummyTask.TYPE, DummyTaskWidget::new);
        registerSimple(EntityInteractTask.TYPE, EntityInteractTaskWidget::new);
        register(XpTask.TYPE, XpTaskWidget::new);
        registerSimple(LocationTask.TYPE, LocationTaskWidget::new);
        registerSimple(StatTask.TYPE, StatTaskWidget::new);
    }

    public interface QuestTaskWidgetSimpleFactory<I, S extends Tag, T extends QuestTask<I, S, T>> extends QuestTaskWidgetFactory<I, S, T> {
        DisplayWidget create(T task, TaskProgress<S> progress);

        @Override
        default DisplayWidget create(String quest, T task, TaskProgress<S> progress, ModUtils.QuestStatus status) {
            return create(task, progress);
        }
    }

    public interface QuestTaskWidgetBasicFactory<I, S extends Tag, T extends QuestTask<I, S, T>> extends QuestTaskWidgetFactory<I, S, T> {
        DisplayWidget create(String quest, T task, TaskProgress<S> progress);

        @Override
        default DisplayWidget create(String quest, T task, TaskProgress<S> progress, ModUtils.QuestStatus status) {
            return create(quest, task, progress);
        }
    }
}
