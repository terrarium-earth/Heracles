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
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestRewardClaimedPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector2i;

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
        QuestDisplay.CODEC.fieldOf("display").orElseGet(() -> QuestDisplay.createDefault(GroupDisplay.createDefault())).forGetter(Quest::display),
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

    public static Quest of(String group, String name, Vector2i location) {
        QuestDisplay display = QuestDisplay.createDefault(new GroupDisplay(group, location));
        display.setTitle(Component.literal(name));
        return new Quest(
            display,
            QuestSettings.createDefault(),
            new HashSet<>(),
            new HashMap<>(),
            new HashMap<>()
        );
    }

    public void claimAllowedRewards(ServerPlayer player, String id) {
        QuestsProgress progresses = QuestProgressHandler.getProgress(player.server, player.getUUID());
        QuestProgress progress = progresses.getProgress(id);
        if (progress == null) return;
        if (!progress.isComplete() && !this.tasks.isEmpty()) return;
        claimRewards(player, id, progresses, progress);
    }

    public void claimRewards(ServerPlayer player, String id, QuestsProgress progresses, QuestProgress progress) {
        if (progress.isClaimed(this)) return;

        claimRewards(
            id,
            player,
            rewards().values().stream().
                filter(QuestReward::canBeMassClaimed)
                .filter(reward -> !progress.claimedRewards().contains(reward.id()))
                .peek(reward -> progresses.claimReward(id, reward.id(), player))
        );
    }

    public void claimAllowedReward(ServerPlayer player, String id, String rewardId) {
        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        if (!progress.isComplete(id) && !this.tasks.isEmpty()) return;
        if (progress.isClaimed(id, this)) return;

        var questProgress = progress.getProgress(id);

        if (rewards.containsKey(rewardId) && !questProgress.claimedRewards().contains(rewardId)) {
            var reward = rewards.get(rewardId);
            if (reward.canBeMassClaimed()) {
                progress.claimReward(id, rewardId, player);
                claimRewards(
                    id,
                    player,
                    Set.of(reward).stream()
                );
            }
        }
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
