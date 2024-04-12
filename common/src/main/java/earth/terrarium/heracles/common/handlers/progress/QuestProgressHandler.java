package earth.terrarium.heracles.common.handlers.progress;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.events.HeraclesEvents;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.teams.TeamProviders;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.SyncQuestProgressPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class QuestProgressHandler extends SavedData {

    private final Map<UUID, QuestsProgress> progress = new HashMap<>();

    public QuestProgressHandler() {
        HeraclesEvents.QuestCompleteListener.register(it -> {
            if (it.quest().settings().autoClaimRewards()) {
                // For avoiding the reward trigger the task checking again.
                it.player().server.execute(() -> {
                    var questsProgress = getProgress(it.player().getUUID());
                    it.quest().claimRewards(it.player(), it.id(), questsProgress, questsProgress.getProgress(it.id()));
                });
            }
        });
    }

    public QuestsProgress getProgress(UUID uuid) {
        return progress.computeIfAbsent(uuid, u -> new QuestsProgress(new HashMap<>()));
    }

    public static QuestsProgress getProgress(MinecraftServer server, UUID uuid) {
        QuestProgressHandler handler = read(server);
        return handler.getProgress(uuid);
    }

    public static void setupChanger() {
        TeamProviders.init((level, uuid) -> {
            List<UUID> members = TeamProviders.getMembers(level, uuid);
            if (members.isEmpty()) return;
            QuestsProgress progress = findFirstPerson(level.getServer(), members);
            if (progress == null) return;
            var currentProgress = getProgress(level.getServer(), uuid);
            copyProgress(progress, currentProgress);
            currentProgress.completableQuests().updateCompleteQuests(currentProgress);
        });
    }

    public static void sync(ServerPlayer player, Collection<String> quests) {
        Map<String, QuestProgress> progress = new LinkedHashMap<>();
        quests.forEach(id -> {
            Quest quest = QuestHandler.get(id);
            if (quest == null) return;
            progress.put(id, QuestProgressHandler.getProgress(player.server, player.getUUID()).getProgress(id));
        });
        NetworkHandler.CHANNEL.sendToPlayer(new SyncQuestProgressPacket(progress), player);
    }

    private static QuestsProgress findFirstPerson(MinecraftServer server, List<UUID> members) {
        for (UUID member : members) {
            QuestsProgress progress = getProgress(server, member);
            if (progress != null) return progress;
        }
        return null;
    }

    private static void copyProgress(QuestsProgress from, QuestsProgress to) {
        from.progress().forEach((id, progress) -> {
            Quest quest = QuestHandler.get(id);
            if (quest == null) return;
            if (quest.settings().individualProgress()) return;
            try {
                to.progress().put(id, new QuestProgress(quest, progress.save()));
            } catch (Exception e) {
                Heracles.LOGGER.error("Failed to copy quest progress for player {}", id, e);
            }
        });
    }

    public void updatePossibleQuests() {
        progress.values().forEach(progress -> progress.completableQuests().updateCompleteQuests(progress));
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        for (var entry : progress.entrySet()) {
            CompoundTag progressTag = new CompoundTag();
            entry.getValue().progress().forEach((id, progress) -> {
                Quest quest = QuestHandler.get(id);
                if (quest == null) return;
                try {
                    progressTag.put(id, progress.save());
                } catch (Exception e) {
                    Heracles.LOGGER.error("Failed to save quest progress for player {}", id, e);
                }
            });
            tag.put(entry.getKey().toString(), progressTag);
        }

        return tag;
    }

    public void load(CompoundTag tag) {
        Set<String> badQuests = new HashSet<>();
        for (var player : tag.getAllKeys()) {
            CompoundTag progress = tag.getCompound(player);
            Map<String, QuestProgress> questProgress = new HashMap<>();
            for (var quest : progress.getAllKeys()) {
                Quest questObj = QuestHandler.get(quest);
                if (questObj == null) {
                    badQuests.add(quest);
                    continue;
                }
                try {
                    QuestProgress progressObj = new QuestProgress(questObj, progress.getCompound(quest));
                    questProgress.put(quest, progressObj);
                } catch (Exception e) {
                    Heracles.LOGGER.error("Failed to load quest progress for player {}", player, e);
                }
                this.progress.put(UUID.fromString(player), new QuestsProgress(questProgress));
            }
        }
        if (!badQuests.isEmpty()) {
            Heracles.LOGGER.error("Failed to load quest progress for quests: {}", String.join(", ", badQuests));
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
