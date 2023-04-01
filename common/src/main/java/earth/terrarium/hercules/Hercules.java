package earth.terrarium.hercules;

import com.mojang.serialization.Codec;
import earth.terrarium.hercules.condition.QuestCondition;
import earth.terrarium.hercules.reward.QuestReward;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.msrandom.extensions.annotations.ImplementedByExtension;
import org.apache.commons.lang3.NotImplementedException;

import java.util.stream.Stream;

public class Hercules {
    public static final String MOD_ID = "hercules";

    public static final ResourceKey<Registry<Criterion>> CRITERION_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Hercules.MOD_ID, "quests/criteria"));

    public static final ResourceKey<Registry<QuestCondition>> QUEST_CONDITION_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Hercules.MOD_ID, "quests/condition"));
    public static final ResourceKey<Registry<Codec<? extends QuestCondition>>> QUEST_CONDITION_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Hercules.MOD_ID, "quest_condition_type"));

    public static final ResourceKey<Registry<QuestReward>> QUEST_REWARD_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Hercules.MOD_ID, "quest_reward_type"));
    public static final ResourceKey<Registry<Codec<? extends QuestReward>>> QUEST_REWARD_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Hercules.MOD_ID, "quests/reward"));

    @ImplementedByExtension
    public static void grantCriteria(ServerPlayer player, Stream<Criterion> criterion) {
        throw new NotImplementedException();
    }

    @ImplementedByExtension
    public static Codec<Codec<? extends QuestCondition>> getConditionRegistryCodec() {
        throw new NotImplementedException();
    }

    @ImplementedByExtension
    public static Codec<Codec<? extends QuestReward>> getRewardRegistryCodec() {
        throw new NotImplementedException();
    }
}
