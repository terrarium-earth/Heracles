package earth.terrarium.heracles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.condition.QuestCondition;
import earth.terrarium.heracles.network.QuestCompletePacket;
import earth.terrarium.heracles.reward.QuestReward;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record Quest(
        ResourceLocation parent,
        QuestCondition condition,
        Component title,
        String description,
        Component rewardText,
        HolderSet<QuestReward> rewards
) {
    private static final Codec<Component> COMPONENT_CODEC = CodecExtras.passthrough(Component.Serializer::toJsonTree, Component.Serializer::fromJson);

    public static Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("parent").forGetter(Quest::parent),
            QuestCondition.CODEC.fieldOf("condition").forGetter(Quest::condition),
            COMPONENT_CODEC.fieldOf("title").forGetter(Quest::title),
            Codec.STRING.fieldOf("description").forGetter(Quest::description),
            COMPONENT_CODEC.fieldOf("reward_text").forGetter(Quest::rewardText),
            QuestReward.LIST_CODEC.fieldOf("rewards").forGetter(Quest::rewards)
    ).apply(instance, Quest::new));

    public void reward(ServerPlayer player) {
        Heracles.NETWORK_CHANNEL.sendToPlayer(
                new QuestCompletePacket(
                        this,
                        rewards().stream().flatMap(reward -> reward.value().reward(player)).distinct().toList()
                ),
                player
        );
    }
}
