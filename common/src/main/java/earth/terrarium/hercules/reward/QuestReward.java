package earth.terrarium.hercules.reward;

import com.mojang.serialization.Codec;
import earth.terrarium.hercules.Hercules;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public interface QuestReward {
    Codec<QuestReward> DIRECT_CODEC = Hercules.getRewardRegistryCodec().dispatchStable(QuestReward::codec, Function.identity());
    Codec<Holder<QuestReward>> CODEC = RegistryFileCodec.create(Hercules.QUEST_REWARD_REGISTRY_KEY, DIRECT_CODEC);

    void reward(ServerPlayer player);

    Codec<? extends QuestReward> codec();
}
