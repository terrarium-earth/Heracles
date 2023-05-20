package earth.terrarium.heracles.api.tasks.client.display;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.defaults.AdvancementTask;
import earth.terrarium.heracles.api.tasks.defaults.CompositeTask;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class TaskTitleFormatters {

    public static void init() {
        TaskTitleFormatter.register(KillEntityQuestTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.target() == 1), task.entity().entityType().getDescription()));
        TaskTitleFormatter.register(GatherItemTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.target() == 1), task.item().getDisplayName(Item::getDescription)));
        TaskTitleFormatter.register(AdvancementTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.advancements().size() == 1)));
        TaskTitleFormatter.register(CompositeTask.TYPE, (task) -> {
            List<Component> titles = new ArrayList<>();
            titles.add(Component.translatable(toTranslationKey(task, task.amount() == 1), task.amount()));
            task.tasks().values().stream().map(TaskTitleFormatter::create).forEach(titles::add);
            return CommonComponents.joinLines(titles);
        });
    }

    private static String toTranslationKey(QuestTask<?, ?, ?> task, boolean singular) {
        ResourceLocation id = task.type().id();
        return "task." + id.getNamespace() + "." + id.getPath() + ".title" + (singular ? ".singular" : ".plural");
    }
}
