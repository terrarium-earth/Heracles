package earth.terrarium.hercules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.hercules.condition.QuestCondition;
import earth.terrarium.hercules.reward.QuestReward;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public record Quest(
        Holder<QuestCondition> condition,
        Component title,
        Component description,
        Component rewardText,
        HolderSet<QuestReward> rewards
) {
    private static final Codec<Component> COMPONENT_CODEC = CodecExtras.passthrough(Component.Serializer::toJsonTree, Component.Serializer::fromJson);

    public static final Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            QuestCondition.CODEC.fieldOf("condition").forGetter(Quest::condition),
            COMPONENT_CODEC.fieldOf("title").forGetter(Quest::title),
            COMPONENT_CODEC.fieldOf("description").forGetter(Quest::description),
            COMPONENT_CODEC.fieldOf("reward_text").forGetter(Quest::rewardText),
            QuestReward.LIST_CODEC.fieldOf("rewards").forGetter(Quest::rewards)
    ).apply(instance, Quest::new));

    public void reward(ServerPlayer player) {
        Hercules.requestQuestCompletedToast(this, rewards().stream().flatMap(reward -> reward.value().reward(player)).distinct()::iterator);
    }
}
