package earth.terrarium.heracles.condition;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.Quest;
import earth.terrarium.heracles.resource.QuestManager;
import earth.terrarium.heracles.team.TeamProvider;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface QuestTask {
    Codec<QuestTask> CODEC = Heracles.getQuestTaskRegistryCodec().dispatch(QuestTask::codec, Function.identity());

    Codec<? extends QuestTask> codec();

    static void completeTasks(ServerPlayer player, Stream<QuestTask> tasks) {
        QuestData data = QuestData.getData(player.server);
        data.getProgress(player).completeTasks(tasks.toList());

        TeamProvider.getAllTeams(player).forEach(team -> {
            List<QuestTask> collectiveCriteria = team.stream()
                    .flatMap(teamPlayer -> data.getProgress(teamPlayer).totalProgress())
                    .distinct()
                    .toList();

            for (Quest quest : QuestManager.INSTANCE.getQuests().values()) {
                if (!quest.condition().isAcquired(collectiveCriteria.stream())) continue;

                team.forEach(quest::reward);
            }
        });
    }
}
