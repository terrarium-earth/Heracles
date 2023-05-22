package earth.terrarium.heracles.common.handlers.syncing;

import com.google.common.collect.Maps;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.common.handlers.pinned.PinnedQuestHandler;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.SyncDescriptionsPacket;
import earth.terrarium.heracles.common.network.packets.quests.SyncQuestsPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public final class QuestSyncer {

    public static void syncToAll(List<ServerPlayer> players) {
        NetworkHandler.CHANNEL.sendToPlayers(createPacket(), players);
        syncDescriptions(players);
    }

    public static void sync(ServerPlayer player) {
        NetworkHandler.CHANNEL.sendToPlayer(createPacket(), player);
        PinnedQuestHandler.sync(player);
        syncDescriptions(List.of(player));
    }

    private static SyncQuestsPacket createPacket() {
        Map<String, Quest> quests = QuestHandler.quests();
        Map<String, Quest> compressedQuests = Maps.newHashMapWithExpectedSize(quests.size());
        for (var entry : quests.entrySet()) {
            compressedQuests.put(entry.getKey(), compress(entry.getValue()));
        }
        return new SyncQuestsPacket(compressedQuests, QuestHandler.groups());
    }

    private static void syncDescriptions(Collection<ServerPlayer> player) {
        var quests = QuestHandler.quests();
        List<QuestDescEntry> descriptions = new ArrayList<>();
        descriptions.add(new QuestDescEntry());
        quests.forEach((key, quest) -> {
            String description = String.join("\n", quest.display().description());
            if (descriptions.get(descriptions.size() - 1).size > 6000000) {
                descriptions.add(new QuestDescEntry());
            }
            descriptions.get(descriptions.size() - 1).add(key, description);
        });
        for (QuestDescEntry description : descriptions) {
            NetworkHandler.CHANNEL.sendToPlayers(new SyncDescriptionsPacket(description.descriptions), player);
        }
    }

    private static Quest compress(Quest quest) {
        QuestDisplay display = new QuestDisplay(
            quest.display().icon(),
            quest.display().iconBackground(),
            quest.display().title(),
            quest.display().subtitle(),
            List.of(),
            quest.display().groups()
        );

        return new Quest(
            display,
            quest.settings(),
            quest.dependencies(),
            quest.tasks(),
            quest.rewards()
        );
    }

    private static class QuestDescEntry {

        Map<String, String> descriptions = new HashMap<>();
        int size = 0;

        public void add(String key, String description) {
            descriptions.put(key, description);
            size += description.getBytes().length;
            size += key.getBytes().length;
        }
    }
}
