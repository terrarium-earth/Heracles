package earth.terrarium.heracles.client.handlers;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.ServerboundAddQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.ServerboundRemoveQuestPacket;

public class ClientQuestNetworking {

    public static ClientQuests.QuestEntry add(String id, Quest quest) {
        ClientQuests.QuestEntry entry = ClientQuests.addQuest(id, quest);
        NetworkHandler.CHANNEL.sendToServer(new ServerboundAddQuestPacket(id, quest));
        return entry;
    }

    public static void remove(String id) {
        ClientQuests.remove(id);
        NetworkHandler.CHANNEL.sendToServer(new ServerboundRemoveQuestPacket(id));
    }
}
