package earth.terrarium.heracles.common.network;

import com.teamresourceful.resourcefullib.common.network.NetworkChannel;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.network.packets.QuestCompletedPacket;
import earth.terrarium.heracles.common.network.packets.QuestRewardClaimedPacket;
import earth.terrarium.heracles.common.network.packets.QuestUnlockedPacket;
import earth.terrarium.heracles.common.network.packets.groups.CreateGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.DeleteGroupPacket;
import earth.terrarium.heracles.common.network.packets.groups.EditGroupPacket;
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

@SuppressWarnings("UnstableApiUsage")
public class NetworkHandler {

    public static final NetworkChannel CHANNEL = new NetworkChannel(Heracles.MOD_ID, 1, "main");

    public static void init() {
        CHANNEL.register(QuestRewardClaimedPacket.TYPE);
        CHANNEL.register(SyncQuestsPacket.TYPE);
        CHANNEL.register(QuestCompletedPacket.TYPE);
        CHANNEL.register(SyncPinnedQuestsPacket.TYPE);
        CHANNEL.register(SyncQuestProgressPacket.TYPE);
        CHANNEL.register(SyncDescriptionsPacket.TYPE);
        CHANNEL.register(OpenQuestScreenPacket.TYPE);
        CHANNEL.register(OpenQuestsScreenPacket.TYPE);
        CHANNEL.register(ClientboundAddQuestPacket.TYPE);
        CHANNEL.register(ClientboundRemoveQuestPacket.TYPE);
        CHANNEL.register(ClientboundUpdateQuestPacket.TYPE);
        CHANNEL.register(QuestUnlockedPacket.TYPE);

        CHANNEL.register(OpenGroupPacket.TYPE);
        CHANNEL.register(OpenQuestPacket.TYPE);
        CHANNEL.register(ServerboundUpdateQuestPacket.TYPE);
        CHANNEL.register(ServerboundAddQuestPacket.TYPE);
        CHANNEL.register(ServerboundRemoveQuestPacket.TYPE);
        CHANNEL.register(CreateGroupPacket.TYPE);
        CHANNEL.register(ClaimRewardsPacket.TYPE);
        CHANNEL.register(SetPinnedQuestPacket.TYPE);
        CHANNEL.register(ClaimSelectableRewardsPacket.TYPE);
        CHANNEL.register(DeleteGroupPacket.TYPE);
        CHANNEL.register(CheckTaskPacket.TYPE);
        CHANNEL.register(ManualItemTaskPacket.TYPE);
        CHANNEL.register(ManualXpTaskPacket.TYPE);
        CHANNEL.registerPacket(EditGroupPacket.TYPE);
    }
}
