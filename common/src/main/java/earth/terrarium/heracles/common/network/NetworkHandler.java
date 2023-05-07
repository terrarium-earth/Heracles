package earth.terrarium.heracles.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.network.packets.OpenGroupPacket;
import earth.terrarium.heracles.common.network.packets.OpenQuestPacket;
import earth.terrarium.heracles.common.network.packets.QuestCompletePacket;
import earth.terrarium.heracles.common.network.packets.SyncQuestsPacket;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Heracles.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestCompletePacket.ID, QuestCompletePacket.HANDLER, QuestCompletePacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncQuestsPacket.ID, SyncQuestsPacket.HANDLER, SyncQuestsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenGroupPacket.ID, OpenGroupPacket.HANDLER, OpenGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenQuestPacket.ID, OpenQuestPacket.HANDLER, OpenQuestPacket.class);
    }
}
