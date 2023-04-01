package earth.terrarium.hercules.reward;

import com.mojang.serialization.Codec;
import earth.terrarium.hercules.Hercules;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.stream.Stream;

public interface QuestReward {
    Codec<QuestReward> DIRECT_CODEC = Hercules.getRewardRegistryCodec().dispatchStable(QuestReward::codec, Function.identity());
    Codec<Holder<QuestReward>> CODEC = RegistryFileCodec.create(Hercules.QUEST_REWARD_REGISTRY_KEY, DIRECT_CODEC);
    Codec<HolderSet<QuestReward>> LIST_CODEC = RegistryCodecs.homogeneousList(Hercules.QUEST_REWARD_REGISTRY_KEY, DIRECT_CODEC);

    Stream<Item> reward(ServerPlayer player);

    Codec<? extends QuestReward> codec();
}
