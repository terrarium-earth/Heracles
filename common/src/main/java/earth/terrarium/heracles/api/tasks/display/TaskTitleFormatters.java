package earth.terrarium.heracles.api.tasks.display;

import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.defaults.ItemQuestTask;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static earth.terrarium.heracles.api.tasks.display.TaskTitleFormatter.register;

public class TaskTitleFormatters {

    public static void init() {
        register(KillEntityQuestTask.TYPE, (task) -> Component.translatable(toTranslationKey(task, task.target() == 1), task.entity().entityType().getDescription()));
        register(ItemQuestTask.TYPE, (task) -> {
            Component title = switch (task.item().size()) {
                case 0 -> CommonComponents.EMPTY;
                case 1 ->
                    task.item().get(0).isBound() ? task.item().get(0).value().getDescription() : CommonComponents.EMPTY;
                default -> Component.literal("???");
            };
            return Component.translatable(toTranslationKey(task, task.target() == 1), title);
        });
    }

    private static String toTranslationKey(QuestTask<?, ?, ?> task, boolean singular) {
        ResourceLocation id = task.type().id();
        return "task." + id.getNamespace() + "." + id.getPath() + ".title" + (singular ? ".singular" : ".plural");
    }
}
