package earth.terrarium.heracles;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.reward.QuestReward;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.msrandom.extensions.annotations.ImplementedByExtension;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Function;
import java.util.stream.Stream;

public class Heracles {
    public static final String MOD_ID = "heracles";

    public static final ResourceKey<Registry<Function<DeserializationContext, Codec<? extends QuestCondition>>>> QUEST_CONDITION_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quest_condition_type"));

    public static final ResourceKey<Registry<QuestReward>> QUEST_REWARD_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quest_reward_type"));
    public static final ResourceKey<Registry<Codec<? extends QuestReward>>> QUEST_REWARD_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(Heracles.MOD_ID, "quests/reward"));

    public static Codec<Criterion> criterionCodec(DeserializationContext deserializationContext) {
        return CodecExtras.passthrough(Criterion::serializeToJson, json -> Criterion.criterionFromJson(json.getAsJsonObject(), deserializationContext));
    }

    @ImplementedByExtension
    public static void grantCriteria(ServerPlayer player, Stream<Criterion> criterion) {
        throw new NotImplementedException();
    }

    @ImplementedByExtension
    public static Codec<Function<DeserializationContext, Codec<? extends QuestCondition>>> getConditionRegistryCodec() {
        throw new NotImplementedException();
    }

    @ImplementedByExtension
    public static Codec<Codec<? extends QuestReward>> getRewardRegistryCodec() {
        throw new NotImplementedException();
    }

    @ImplementedByExtension
    public static void requestQuestCompletedToast(Quest quest, Iterable<Item> items) {
        throw new NotImplementedException();
    }
}
