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
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestRewardClaimedPacket;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Quest(
    QuestDisplay display,

    QuestSettings settings,

    Set<String> dependencies,

    List<QuestTask<?, ?>> tasks,
    Component rewardText,
    List<QuestReward<?>> rewards
) {

    public static Codec<Quest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        QuestDisplay.CODEC.fieldOf("display").forGetter(Quest::display),
        QuestSettings.CODEC.fieldOf("settings").orElse(QuestSettings.createDefault()).forGetter(Quest::settings),
        CodecExtras.set(Codec.STRING).fieldOf("dependencies").orElse(new HashSet<>()).forGetter(Quest::dependencies),
        QuestTasks.CODEC.listOf().fieldOf("tasks").orElse(new ArrayList<>()).forGetter(Quest::tasks),
        ExtraCodecs.COMPONENT.fieldOf("reward_text").orElse(CommonComponents.EMPTY).forGetter(Quest::rewardText),
        QuestRewards.CODEC.listOf().fieldOf("rewards").orElse(new ArrayList<>()).forGetter(Quest::rewards)
    ).apply(instance, Quest::new));

    public void reward(ServerPlayer player) {
        QuestsProgress progress = QuestProgressHandler.getProgress(player.server, player.getUUID());
        String id = QuestHandler.getKey(this);
        if (!progress.isComplete(id)) return;
        if (progress.isClaimed(id)) return;

        NetworkHandler.CHANNEL.sendToPlayer(
            new QuestRewardClaimedPacket(
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
