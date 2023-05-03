package earth.terrarium.heracles.resource;

import com.google.common.collect.BiMap;
import earth.terrarium.heracles.condition.QuestTask;
import net.minecraft.resources.ResourceLocation;

public class QuestTaskManager extends CodecResourceReloadListener<QuestTask> {
    public static final QuestTaskManager INSTANCE = new QuestTaskManager();

    public QuestTaskManager() {
        super(QuestTask.CODEC, "quests/task");
    }

    public BiMap<ResourceLocation, QuestTask> getTasks() {
        return getValues();
    }
}
