package earth.terrarium.heracles.api.tasks.client.display;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.defaults.AdvancementTask;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class TaskTitleFormatters {

    public static void init() {
        TaskTitleFormatter.register(KillEntityQuestTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.target() == 1), task.entity().entityType().getDescription()));
        TaskTitleFormatter.register(GatherItemTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.target() == 1), task.item().getDisplayName(Item::getDescription)));
        TaskTitleFormatter.register(AdvancementTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.advancements().size() == 1)));
    }

    private static String toTranslationKey(QuestTask<?, ?, ?> task, boolean singular) {
        ResourceLocation id = task.type().id();
        return "task." + id.getNamespace() + "." + id.getPath() + ".title" + (singular ? ".singular" : ".plural");
    }
}
