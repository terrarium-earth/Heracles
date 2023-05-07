package earth.terrarium.heracles.common.handlers.syncing;

import com.google.common.collect.Maps;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.SyncQuestsPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;

public final class QuestSyncer {

    public static void syncToAll(List<ServerPlayer> players) {
        NetworkHandler.CHANNEL.sendToPlayers(createPacket(), players);
    }

    public static void sync(ServerPlayer player) {
        NetworkHandler.CHANNEL.sendToPlayer(createPacket(), player);
    }

    private static SyncQuestsPacket createPacket() {
        Map<String, Quest> quests = QuestHandler.quests();
        Map<String, Quest> compressedQuests = Maps.newHashMapWithExpectedSize(quests.size());
        for (var entry : quests.entrySet()) {
            compressedQuests.put(entry.getKey(), compress(entry.getValue()));
        }
        return new SyncQuestsPacket(compressedQuests, QuestHandler.groups());
    }

    private static Quest compress(Quest quest) {
        return new Quest(
            quest.icon(),
            quest.title(),
            "",
            quest.position(),
            quest.group(),
            quest.dependencies(),
            quest.tasks(),
            quest.rewardText(),
            quest.rewards()
        );
    }
}
