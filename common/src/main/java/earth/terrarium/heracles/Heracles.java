package earth.terrarium.heracles;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.networking.NetworkChannel;
import com.teamresourceful.resourcefullib.common.networking.base.NetworkDirection;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.network.QuestCompletePacket;
import earth.terrarium.heracles.reward.QuestReward;
import earth.terrarium.heracles.team.TeamProvider;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.extensions.annotations.ImplementedByExtension;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Function;

public class Heracles {
    public static final String MOD_ID = "heracles";
    public static final NetworkChannel NETWORK_CHANNEL = new NetworkChannel(MOD_ID, 1, "main");

    public static final ResourceKey<Registry<QuestCondition.QuestConditionCodec<?>>> QUEST_CONDITION_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quest_condition_type"));

    public static final ResourceKey<Registry<QuestReward>> QUEST_REWARD_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quest_reward_type"));
    public static final ResourceKey<Registry<Codec<? extends QuestReward>>> QUEST_REWARD_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quests/reward"));
    public static final ResourceKey<Registry<TeamProvider>> TEAM_PROVIDER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "team_provider"));

    public static void init() {
        NETWORK_CHANNEL.registerPacket(NetworkDirection.SERVER_TO_CLIENT, QuestCompletePacket.ID, QuestCompletePacket.HANDLER, QuestCompletePacket.class);
    }

    @ImplementedByExtension
    public static Codec<QuestCondition.QuestConditionCodec<?>> getConditionRegistryCodec() {
        throw new NotImplementedException();
    }

    @ImplementedByExtension
    public static Codec<Codec<? extends QuestReward>> getRewardRegistryCodec() {
        throw new NotImplementedException();
    }

    @ImplementedByExtension
    public static Iterable<TeamProvider> getTeamProviders() {
        throw new NotImplementedException();
    }
}
