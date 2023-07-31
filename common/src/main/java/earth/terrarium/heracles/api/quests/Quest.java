package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTasks;
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestRewardClaimedPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;
import java.util.stream.Stream;

public record Quest(
    QuestDisplay display,

    QuestSettings settings,

    Set<String> dependencies,

    Map<String, QuestTask<?, ?, ?>> tasks,
    Map<String, QuestReward<?>> rewards
) {

    public static Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestDisplay.CODEC.fieldOf("display").orElseGet(() -> QuestDisplay.createDefault(GroupDisplay.createDefault(), "New Quest")).forGetter(Quest::display),
        QuestSettings.CODEC.fieldOf("settings").orElseGet(QuestSettings::createDefault).forGetter(Quest::settings),
        CodecExtras.set(Codec.STRING).fieldOf("dependencies").orElse(new HashSet<>()).forGetter(Quest::dependencies),
        QuestTasks.CODEC.fieldOf("tasks").orElse(new HashMap<>()).forGetter(Quest::tasks),
        QuestRewards.CODEC.fieldOf("rewards").orElse(new HashMap<>()).forGetter(Quest::rewards)
    ).apply(instance, Quest::fromCodec));

    /**
     * This method is used by the codec to create a new quest instance.
     * This is needed as codecs make immutable objects, and we need to be able to add tasks and rewards to the quest.
     */
    private static Quest fromCodec(QuestDisplay display, QuestSettings settings, Set<String> dependencies, Map<String, QuestTask<?, ?, ?>> tasks, Map<String, QuestReward<?>> rewards) {
        return new Quest(display, settings, dependencies, new HashMap<>(tasks), new HashMap<>(rewards));
    }

    public void claimAllowedRewards(ServerPlayer player, String id) {
        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        if (!progress.isComplete(id) && !this.tasks.isEmpty()) return;
        if (progress.isClaimed(id, this)) return;

        var questProgress = progress.getProgress(id);

        claimRewards(
            id,
            player,
            rewards().values().stream().
                filter(QuestReward::canBeMassClaimed)
                .filter(reward -> !questProgress.claimedRewards().contains(reward.id()))
                .peek(reward -> questProgress.claimReward(reward.id()))
        );
    }

    public void claimRewards(String questId, ServerPlayer player, Stream<? extends QuestReward<?>> rewards) {
        List<Item> items = rewards
            .flatMap(reward -> reward.reward(player))
            .filter(stack -> !stack.is(Items.AIR))
            .map(ItemStack::getItem)
            .distinct()
            .toList();
        if (!items.isEmpty()) {
            NetworkHandler.CHANNEL.sendToPlayer(
                new QuestRewardClaimedPacket(questId, items),
                player
            );
        }
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();
    }
}
