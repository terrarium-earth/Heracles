package earth.terrarium.heracles.reward;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.Heracles;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.stream.Stream;

public interface QuestReward {
    Codec<QuestReward> DIRECT_CODEC = Heracles.getRewardRegistryCodec().dispatchStable(QuestReward::codec, Function.identity());
    Codec<HolderSet<QuestReward>> LIST_CODEC = RegistryCodecs.homogeneousList(Heracles.QUEST_REWARD_REGISTRY_KEY, DIRECT_CODEC);

    Stream<Item> reward(ServerPlayer player);

    Codec<? extends QuestReward> codec();
}
