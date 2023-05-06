package earth.terrarium.heracles.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.common.handlers.QuestHandler;
import earth.terrarium.heracles.common.handlers.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.QuestsProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestCompletePacket;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public record Quest(
    String parent,
    Component title,
    String description,

    Set<String> dependencies,

    List<QuestTask<?, ?>> tasks,
    Component rewardText,
    List<QuestReward<?>> rewards
) {

    public static Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.optionalFieldOf("parent").forGetter(quest -> Optional.ofNullable(quest.parent())),
        ExtraCodecs.COMPONENT.fieldOf("title").orElse(Component.literal("New Quest")).forGetter(Quest::title),
        Codec.STRING.fieldOf("description").orElse("").forGetter(Quest::description),
        CodecExtras.set(Codec.STRING).fieldOf("dependencies").orElse(new HashSet<>()).forGetter(Quest::dependencies),
        QuestTasks.CODEC.listOf().fieldOf("tasks").orElse(new ArrayList<>()).forGetter(Quest::tasks),
        ExtraCodecs.COMPONENT.fieldOf("reward_text").orElse(CommonComponents.EMPTY).forGetter(Quest::rewardText),
        QuestRewards.CODEC.listOf().fieldOf("rewards").orElse(new ArrayList<>()).forGetter(Quest::rewards)
    ).apply(instance, Quest::new));

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Quest(Optional<String> parent, Component title, String description, Set<String> dependencies, List<QuestTask<?, ?>> tasks, Component rewardText, List<QuestReward<?>> rewards) {
        this(parent.orElse(null), title, description, dependencies, tasks, rewardText, rewards);
    }

    public void reward(ServerPlayer player) {
        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        String id = QuestHandler.getKey(this);
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
