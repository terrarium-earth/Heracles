package earth.terrarium.heracles.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.network.packets.QuestCompletePacket;
import earth.terrarium.heracles.network.packets.SyncQuestsPacket;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Heracles.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestCompletePacket.ID, QuestCompletePacket.HANDLER, QuestCompletePacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncQuestsPacket.ID, SyncQuestsPacket.HANDLER, SyncQuestsPacket.class);
    }
}
