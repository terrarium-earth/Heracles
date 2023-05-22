package earth.terrarium.heracles.api.client.settings.tasks;

import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.RestrictedEntityPredicate;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.IntSetting;
import earth.terrarium.heracles.api.client.settings.base.RegistrySetting;
import earth.terrarium.heracles.api.tasks.defaults.KillEntityQuestTask;
import net.minecraft.Optionull;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class KillEntityTaskSettings implements SettingInitializer<KillEntityQuestTask> {

    public static final KillEntityTaskSettings INSTANCE = new KillEntityTaskSettings();

    @Override
    public CreationData create(@Nullable KillEntityQuestTask object) {
        CreationData settings = new CreationData();
        settings.put("entity", RegistrySetting.ENTITY, getDefaultEntity(object));
        settings.put("amount", IntSetting.ONE, getDefaultAmount(object));
        return settings;
    }

    @Override
    public KillEntityQuestTask create(String id, KillEntityQuestTask object, Data data) {
        EntityType<?> entityType = data.get("entity", RegistrySetting.ENTITY).orElse(getDefaultEntity(object));
        RestrictedEntityPredicate old = Optionull.map(object, KillEntityQuestTask::entity);

        RestrictedEntityPredicate entity = new RestrictedEntityPredicate(
            entityType,
            Optionull.mapOrDefault(old, RestrictedEntityPredicate::location, LocationPredicate.ANY),
            Optionull.mapOrDefault(old, RestrictedEntityPredicate::effects, MobEffectsPredicate.ANY),
            Optionull.mapOrDefault(old, RestrictedEntityPredicate::nbt, NbtPredicate.ANY),
            Optionull.mapOrDefault(old, RestrictedEntityPredicate::flags, EntityFlagsPredicate.ANY),
            Optionull.mapOrDefault(old, RestrictedEntityPredicate::targetedEntity, EntityPredicate.ANY)
        );

        return new KillEntityQuestTask(id, entity, data.get("amount", IntSetting.ONE).orElse(1));
    }

    private static EntityType<?> getDefaultEntity(KillEntityQuestTask object) {
        return Optionull.mapOrDefault(object, task -> task.entity().entityType(), EntityType.PIG);
    }

    private static int getDefaultAmount(KillEntityQuestTask object) {
        return Optionull.mapOrDefault(object, KillEntityQuestTask::target, 1);
    }
}
