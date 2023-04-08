package earth.terrarium.heracles.condition;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.Quest;
import earth.terrarium.heracles.resource.QuestManager;
import earth.terrarium.heracles.team.TeamProvider;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.server.level.ServerPlayer;
import net.msrandom.extensions.annotations.ImplementedByExtension;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;
import java.util.stream.Stream;

public class Criteria {
    public static Codec<Criterion> criterionCodec(DeserializationContext deserializationContext) {
        return CodecExtras.passthrough(Criterion::serializeToJson, json -> Criterion.criterionFromJson(json.getAsJsonObject(), deserializationContext));
    }

    public static void grantCriteria(ServerPlayer player, Stream<Criterion> criteria) {
        getAcquiredCriteria(player).acquireCriteria(criteria);

        TeamProvider.getAllTeams(player).forEach(team -> {
            List<Criterion> collectiveCriteria = team.stream()
                    .flatMap(teamPlayer -> getAcquiredCriteria(teamPlayer).getAcquiredCriteria())
                    .distinct()
                    .toList();

            for (Quest quest : QuestManager.getInstance().getQuests().values()) {
                if (!quest.condition().isAcquired(collectiveCriteria.stream())) continue;

                team.forEach(quest::reward);
            }
        });
    }

    @ImplementedByExtension
    public static PlayerAcquiredCriteria getAcquiredCriteria(ServerPlayer player) {
        throw new NotImplementedException();
    }
}
