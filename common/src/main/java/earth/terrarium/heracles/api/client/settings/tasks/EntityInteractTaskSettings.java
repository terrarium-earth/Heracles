package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.tasks.defaults.EntityInteractTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

public class EntityInteractTaskSettings implements SettingInitializer<EntityInteractTask> {

    public static final EntityInteractTaskSettings INSTANCE = new EntityInteractTaskSettings();

    @Override
    public CreationData create(@Nullable EntityInteractTask object) {
        CreationData settings = new CreationData();
        settings.put("entity", RegistryValueSetting.ENTITY, getDefaultEntity(object));
        return settings;
    }

    @Override
    public EntityInteractTask create(String id, EntityInteractTask object, Data data) {
        RegistryValue<EntityType<?>> entity = data.get("entity", RegistryValueSetting.ENTITY).orElse(getDefaultEntity(object));
        NbtPredicate old = Optionull.mapOrDefault(object, EntityInteractTask::nbt, NbtPredicate.ANY);
        return new EntityInteractTask(id, entity, old);
    }

    private static RegistryValue<EntityType<?>> getDefaultEntity(EntityInteractTask object) {
        return Optionull.mapOrDefault(object, EntityInteractTask::entity, new RegistryValue<>(Either.left(EntityType.PIG.builtInRegistryHolder())));
    }
}
