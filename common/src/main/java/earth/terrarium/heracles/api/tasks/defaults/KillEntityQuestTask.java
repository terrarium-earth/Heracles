package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedEntityPredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.IntegerTaskStorage;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public record KillEntityQuestTask(
    String id, String title, QuestIcon<?> icon, RestrictedEntityPredicate entity, int target
) implements QuestTask<LivingEntity, NumericTag, KillEntityQuestTask>, CustomizableQuestElement {

    public static final QuestTaskType<KillEntityQuestTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, LivingEntity input) {
        int current = storage().readInt(progress);
        if (input.level() instanceof ServerLevel level && entity.matches(level, input)) {
            return IntTag.valueOf(current + 1);
        }
        return IntTag.valueOf(current);
    }

    @Override
    public float getProgress(NumericTag progress) {
        return storage().readInt(progress) / (float) target;
    }

    @Override
    public IntegerTaskStorage storage() {
        return IntegerTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<KillEntityQuestTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<KillEntityQuestTask> {

        @Override
        public ResourceLocation id() {
            return ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "kill_entity");
        }

        @Override
        public MapCodec<KillEntityQuestTask> codec(String id) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.lenientOptionalFieldOf("title", "").forGetter(KillEntityQuestTask::title),
                QuestIcons.CODEC.lenientOptionalFieldOf("icon", ItemQuestIcon.AIR).forGetter(KillEntityQuestTask::icon),
                CODEC.fieldOf("entity").forGetter(KillEntityQuestTask::entity),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(KillEntityQuestTask::target)
            ).apply(instance, KillEntityQuestTask::new));
        }
    }

    public static final Codec<RestrictedEntityPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(RestrictedEntityPredicate::entityType),
        LocationPredicate.CODEC.lenientOptionalFieldOf("location").forGetter(RestrictedEntityPredicate::location),
        MobEffectsPredicate.CODEC.lenientOptionalFieldOf("effects").forGetter(RestrictedEntityPredicate::effects),
        NbtPredicate.CODEC.lenientOptionalFieldOf("nbt").forGetter(RestrictedEntityPredicate::nbt),
        EntityFlagsPredicate.CODEC.lenientOptionalFieldOf("flags").forGetter(RestrictedEntityPredicate::flags),
        EntityPredicate.CODEC.lenientOptionalFieldOf("target").forGetter(RestrictedEntityPredicate::targetedEntity)
    ).apply(instance, RestrictedEntityPredicate::new));
}
