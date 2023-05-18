package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestRewardClaimedPacket;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record Quest(
    QuestDisplay display,

    QuestSettings settings,

    Set<String> dependencies,

    Map<String, QuestTask<?, ?, ?>> tasks,
    Component rewardText,
    Map<String, QuestReward<?>> rewards
) {

    public static Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestDisplay.CODEC.fieldOf("display").forGetter(Quest::display),
        QuestSettings.CODEC.fieldOf("settings").orElse(QuestSettings.createDefault()).forGetter(Quest::settings),
        CodecExtras.set(Codec.STRING).fieldOf("dependencies").orElse(new HashSet<>()).forGetter(Quest::dependencies),
        QuestTasks.CODEC.fieldOf("tasks").orElse(new HashMap<>()).forGetter(Quest::tasks),
        ExtraCodecs.COMPONENT.fieldOf("reward_text").orElse(CommonComponents.EMPTY).forGetter(Quest::rewardText),
        QuestRewards.CODEC.fieldOf("rewards").orElse(new HashMap<>()).forGetter(Quest::rewards)
    ).apply(instance, Quest::fromCodec));

    /**
     * This method is used by the codec to create a new quest instance.
     * This is needed as codecs make immutable objects and we need to be able to add tasks and rewards to the quest.
     */
    private static Quest fromCodec(QuestDisplay display, QuestSettings settings, Set<String> dependencies, Map<String, QuestTask<?, ?, ?>> tasks, Component rewardText, Map<String, QuestReward<?>> rewards) {
        return new Quest(display, settings, dependencies, new HashMap<>(tasks), rewardText, new HashMap<>(rewards));
    }

    public void reward(ServerPlayer player) {
        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        String id = QuestHandler.getKey(this);
        if (!progress.isComplete(id)) return;
        if (progress.isClaimed(id, this)) return;

        QuestProgress questProgress = progress.getProgress(id);

        NetworkHandler.CHANNEL.sendToPlayer(
            new QuestRewardClaimedPacket(
                this,
                rewards()
                    .values()
                    .stream()
                    .filter(reward -> !questProgress.claimedRewards().contains(reward.id()))
                    .peek(reward -> questProgress.claimReward(reward.id()))
                    .flatMap(reward -> reward.reward(player))
                    .filter(stack -> !stack.is(Items.AIR))
                    .map(ItemStack::getItem)
                    .distinct()
                    .toList()
            ),
            player
        );
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();
    }
}
