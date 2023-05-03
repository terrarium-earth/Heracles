package earth.terrarium.heracles;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.condition.QuestTask;
import earth.terrarium.heracles.network.NetworkHandler;
import earth.terrarium.heracles.common.regisitries.ModMenus;
import earth.terrarium.heracles.reward.QuestReward;
import earth.terrarium.heracles.team.TeamProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.msrandom.extensions.annotations.ImplementedByExtension;
import org.apache.commons.lang3.NotImplementedException;

public class Heracles {
    public static final String MOD_ID = "heracles";

    public static final ResourceKey<Registry<Codec<? extends QuestCondition>>> QUEST_CONDITION_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quest_condition_type"));

    public static final ResourceKey<Registry<QuestReward>> QUEST_REWARD_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quest_reward_type"));
    public static final ResourceKey<Registry<Codec<? extends QuestReward>>> QUEST_REWARD_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quests/reward"));
    public static final ResourceKey<Registry<TeamProvider>> TEAM_PROVIDER_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "team_provider"));

    public static void init() {
        ModMenus.MENUS.init();
        NetworkHandler.init();
    }

    @ImplementedByExtension
    public static Codec<Codec<? extends QuestCondition>> getConditionRegistryCodec() {
        throw new NotImplementedException();
    }

    @ImplementedByExtension
    public static Codec<Codec<? extends QuestTask>> getQuestTaskRegistryCodec() {
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
