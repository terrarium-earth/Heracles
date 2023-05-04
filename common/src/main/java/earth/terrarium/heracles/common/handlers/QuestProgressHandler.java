package earth.terrarium.heracles.common.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestProgressHandler extends SavedData {

    private final Map<UUID, QuestsProgress> progress = new HashMap<>();

    public QuestProgressHandler() {
    }

    public QuestsProgress getProgress(UUID uuid) {
        return progress.computeIfAbsent(uuid, u -> new QuestsProgress(new HashMap<>()));
    }

    public static QuestsProgress getProgress(MinecraftServer server, UUID uuid) {
        QuestProgressHandler handler = read(server);
        return handler.getProgress(uuid);
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        for (var entry : progress.entrySet()) {
            CompoundTag progress = new CompoundTag();
            for (var progressEntry : entry.getValue().progress().entrySet()) {
                CompoundTag questProgress = new CompoundTag();
                CompoundTag tasksProgress = new CompoundTag();
                if (progressEntry.getValue().isClaimed()) {
                    questProgress.putBoolean("claimed", true);
                }
                if (progressEntry.getValue().isComplete()) {
                    questProgress.putBoolean("complete", true);
                }
                progressEntry.getValue().tasks().forEach((taskId, taskProgress) -> {
                    CompoundTag task = new CompoundTag();
                    task.putInt("progress", taskProgress.progress());
                    task.putBoolean("complete", taskProgress.isComplete());
                    tasksProgress.put(taskId, task);
                });
                questProgress.put("tasks", tasksProgress);
                progress.put(progressEntry.getKey().toString(), questProgress);
            }
            tag.put(entry.getKey().toString(), progress);
        }

        return tag;
    }

    public void load(CompoundTag tag) {
        for (var player : tag.getAllKeys()) {
            CompoundTag progress = tag.getCompound(player);
            Map<ResourceLocation, QuestProgress> questProgress = new HashMap<>();
            for (var quest : progress.getAllKeys()) {
                CompoundTag questTag = progress.getCompound(quest);
                Map<String, TaskProgress> tasks = new HashMap<>();
                for (var task : questTag.getAllKeys()) {
                    CompoundTag taskTag = questTag.getCompound(task);
                    tasks.put(task, new TaskProgress(
                        taskTag.getInt("progress"),
                        taskTag.getBoolean("complete")
                    ));
                }
                questProgress.put(new ResourceLocation(quest), new QuestProgress(
                    questTag.getBoolean("claimed"),
                    questTag.getBoolean("complete"),
                    tasks
                ));
            }
            this.progress.put(UUID.fromString(player), new QuestsProgress(questProgress));
        }
    }

    public static QuestProgressHandler read(MinecraftServer server) {
        return server
            .overworld()
            .getDataStorage()
            .computeIfAbsent(tag -> {
                QuestProgressHandler handler = new QuestProgressHandler();
                handler.load(tag);
                return handler;
            }, QuestProgressHandler::new, "heracles_quest_progress");
    }
}
