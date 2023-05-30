package earth.terrarium.heracles.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.network.packets.QuestCompletedPacket;
import earth.terrarium.heracles.common.network.packets.QuestRewardClaimedPacket;
import earth.terrarium.heracles.common.network.packets.groups.CreateGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.DeleteGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.heracles.common.network.packets.pinned.SetPinnedQuestPacket;
import earth.terrarium.heracles.common.network.packets.pinned.SyncPinnedQuestsPacket;
import earth.terrarium.heracles.common.network.packets.quests.*;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardsPacket;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimSelectableRewardsPacket;
import earth.terrarium.heracles.common.network.packets.tasks.CheckTaskPacket;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Heracles.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestRewardClaimedPacket.ID, QuestRewardClaimedPacket.HANDLER, QuestRewardClaimedPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncQuestsPacket.ID, SyncQuestsPacket.HANDLER, SyncQuestsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestCompletedPacket.ID, QuestCompletedPacket.HANDLER, QuestCompletedPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncPinnedQuestsPacket.ID, SyncPinnedQuestsPacket.HANDLER, SyncPinnedQuestsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncDescriptionsPacket.ID, SyncDescriptionsPacket.HANDLER, SyncDescriptionsPacket.class);

        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, QuestActionPacket.ID, QuestActionPacket.HANDLER, QuestActionPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, UploadQuestPacket.ID, UploadQuestPacket.HANDLER, UploadQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenGroupPacket.ID, OpenGroupPacket.HANDLER, OpenGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenQuestPacket.ID, OpenQuestPacket.HANDLER, OpenQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, CreateGroupPacket.ID, CreateGroupPacket.HANDLER, CreateGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ClaimRewardsPacket.ID, ClaimRewardsPacket.HANDLER, ClaimRewardsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SetPinnedQuestPacket.ID, SetPinnedQuestPacket.HANDLER, SetPinnedQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ClaimSelectableRewardsPacket.ID, ClaimSelectableRewardsPacket.HANDLER, ClaimSelectableRewardsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, DeleteQuestPacket.ID, DeleteQuestPacket.HANDLER, DeleteQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, DeleteGroupPacket.ID, DeleteGroupPacket.HANDLER, DeleteGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, CheckTaskPacket.ID, CheckTaskPacket.HANDLER, CheckTaskPacket.class);
    }
}
