package earth.terrarium.heracles.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.network.packets.*;
import earth.terrarium.heracles.common.network.packets.pinned.SetPinnedQuestPacket;
import earth.terrarium.heracles.common.network.packets.pinned.SyncPinnedQuestsPacket;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Heracles.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestRewardClaimedPacket.ID, QuestRewardClaimedPacket.HANDLER, QuestRewardClaimedPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncQuestsPacket.ID, SyncQuestsPacket.HANDLER, SyncQuestsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestCompletedPacket.ID, QuestCompletedPacket.HANDLER, QuestCompletedPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncPinnedQuestsPacket.ID, SyncPinnedQuestsPacket.HANDLER, SyncPinnedQuestsPacket.class);

        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, QuestActionPacket.ID, QuestActionPacket.HANDLER, QuestActionPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, UploadQuestPacket.ID, UploadQuestPacket.HANDLER, UploadQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenGroupPacket.ID, OpenGroupPacket.HANDLER, OpenGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenQuestPacket.ID, OpenQuestPacket.HANDLER, OpenQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, CreateGroupPacket.ID, CreateGroupPacket.HANDLER, CreateGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ClaimRewardsPacket.ID, ClaimRewardsPacket.HANDLER, ClaimRewardsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SetPinnedQuestPacket.ID, SetPinnedQuestPacket.HANDLER, SetPinnedQuestPacket.class);
    }
}
