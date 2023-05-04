package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedEntityPredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public record KillEntityQuestTask(
    String id, RestrictedEntityPredicate entity, int target
) implements QuestTask<LivingEntity, KillEntityQuestTask> {

    public static final QuestTaskType<KillEntityQuestTask> SERIALIZER = new Type();

    @Override
    public int test(LivingEntity input) {
        if (input.level instanceof ServerLevel level) {
            return entity.matches(level, input) ? 1 : 0;
        }
        return 0;
    }

    @Override
    public QuestTaskType<KillEntityQuestTask> type() {
        return SERIALIZER;
    }

    private static class Type implements QuestTaskType<KillEntityQuestTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "kill_entity");
        }

        @Override
        public Codec<KillEntityQuestTask> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(KillEntityQuestTask::id),
                RestrictedEntityPredicate.CODEC.fieldOf("entity").forGetter(KillEntityQuestTask::entity),
                Codec.INT.fieldOf("amount").forGetter(KillEntityQuestTask::target)
            ).apply(instance, KillEntityQuestTask::new));
        }
    }
}
