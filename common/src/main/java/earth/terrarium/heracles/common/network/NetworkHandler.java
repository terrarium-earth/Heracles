package earth.terrarium.heracles.common.network;

import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.network.packets.QuestCompletedPacket;
import earth.terrarium.heracles.common.network.packets.QuestRewardClaimedPacket;
import earth.terrarium.heracles.common.network.packets.QuestUnlockedPacket;
import earth.terrarium.heracles.common.network.packets.groups.CreateGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.DeleteGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.heracles.common.network.packets.pinned.SetPinnedQuestPacket;
import earth.terrarium.heracles.common.network.packets.pinned.SyncPinnedQuestsPacket;
import earth.terrarium.heracles.common.network.packets.quests.*;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardsPacket;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimSelectableRewardsPacket;
import earth.terrarium.heracles.common.network.packets.screens.OpenQuestScreenPacket;
import earth.terrarium.heracles.common.network.packets.screens.OpenQuestsScreenPacket;
import earth.terrarium.heracles.common.network.packets.tasks.CheckTaskPacket;
import earth.terrarium.heracles.common.network.packets.tasks.ManualItemTaskPacket;
import earth.terrarium.heracles.common.network.packets.tasks.ManualXpTaskPacket;

public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Heracles.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestRewardClaimedPacket.ID, QuestRewardClaimedPacket.HANDLER, QuestRewardClaimedPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncQuestsPacket.ID, SyncQuestsPacket.HANDLER, SyncQuestsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestCompletedPacket.ID, QuestCompletedPacket.HANDLER, QuestCompletedPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncPinnedQuestsPacket.ID, SyncPinnedQuestsPacket.HANDLER, SyncPinnedQuestsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, SyncDescriptionsPacket.ID, SyncDescriptionsPacket.HANDLER, SyncDescriptionsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenQuestScreenPacket.ID, OpenQuestScreenPacket.HANDLER, OpenQuestScreenPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, OpenQuestsScreenPacket.ID, OpenQuestsScreenPacket.HANDLER, OpenQuestsScreenPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, ClientboundAddQuestPacket.ID, ClientboundAddQuestPacket.HANDLER, ClientboundAddQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, ClientboundRemoveQuestPacket.ID, ClientboundRemoveQuestPacket.HANDLER, ClientboundRemoveQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, ClientboundUpdateQuestPacket.ID, ClientboundUpdateQuestPacket.HANDLER, ClientboundUpdateQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestUnlockedPacket.ID, QuestUnlockedPacket.HANDLER, QuestUnlockedPacket.class);

        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenGroupPacket.ID, OpenGroupPacket.HANDLER, OpenGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, OpenQuestPacket.ID, OpenQuestPacket.HANDLER, OpenQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ServerboundUpdateQuestPacket.ID, ServerboundUpdateQuestPacket.HANDLER, ServerboundUpdateQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ServerboundAddQuestPacket.ID, ServerboundAddQuestPacket.HANDLER, ServerboundAddQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ServerboundRemoveQuestPacket.ID, ServerboundRemoveQuestPacket.HANDLER, ServerboundRemoveQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, CreateGroupPacket.ID, CreateGroupPacket.HANDLER, CreateGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ClaimRewardsPacket.ID, ClaimRewardsPacket.HANDLER, ClaimRewardsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, SetPinnedQuestPacket.ID, SetPinnedQuestPacket.HANDLER, SetPinnedQuestPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ClaimSelectableRewardsPacket.ID, ClaimSelectableRewardsPacket.HANDLER, ClaimSelectableRewardsPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, DeleteGroupPacket.ID, DeleteGroupPacket.HANDLER, DeleteGroupPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, CheckTaskPacket.ID, CheckTaskPacket.HANDLER, CheckTaskPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ManualItemTaskPacket.ID, ManualItemTaskPacket.HANDLER, ManualItemTaskPacket.class);
        CHANNEL.registerPacket(NetworkDirection.CLIENT_TO_SERVER, ManualXpTaskPacket.ID, ManualXpTaskPacket.HANDLER, ManualXpTaskPacket.class);
    }
}
