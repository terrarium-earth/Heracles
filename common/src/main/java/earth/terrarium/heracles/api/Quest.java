package earth.terrarium.heracles.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.common.handlers.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.QuestsProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestCompletePacket;
import earth.terrarium.heracles.common.resource.QuestManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public record Quest(
    ResourceLocation parent,
    Component title,
    String description,

    List<QuestTask<?, ?>> tasks,
    Component rewardText,
    List<QuestReward<?>> rewards
) {
    private static final Codec<Component> COMPONENT_CODEC = CodecExtras.passthrough(Component.Serializer::toJsonTree, Component.Serializer::fromJson);

    public static Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("parent").forGetter(Quest::parent),
        COMPONENT_CODEC.fieldOf("title").forGetter(Quest::title),
        Codec.STRING.fieldOf("description").forGetter(Quest::description),
        QuestTasks.CODEC.listOf().fieldOf("tasks").forGetter(Quest::tasks),
        COMPONENT_CODEC.fieldOf("reward_text").forGetter(Quest::rewardText),
        QuestRewards.CODEC.listOf().fieldOf("rewards").forGetter(Quest::rewards)
    ).apply(instance, Quest::new));

    public void reward(ServerPlayer player) {
        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        ResourceLocation id = QuestManager.INSTANCE.getKey(this);
        if (!progress.isComplete(id)) return;
        if (progress.isClaimed(id)) return;

        NetworkHandler.CHANNEL.sendToPlayer(
            new QuestCompletePacket(
                this,
                rewards().stream()
                    .flatMap(reward -> reward.reward(player))
                    .filter(stack -> !stack.is(Items.AIR))
                    .map(ItemStack::getItem)
                    .distinct()
                    .toList()
            ),
            player
        );
    }
}
