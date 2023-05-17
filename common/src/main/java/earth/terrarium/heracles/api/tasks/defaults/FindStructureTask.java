package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Collection;

public record FindStructureTask(
    String id, RegistryValue<Structure> structures
) implements QuestTask<Collection<Structure>, ByteTag, FindStructureTask> {

    public static final QuestTaskType<FindStructureTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Collection<Structure> input) {
        final RegistryAccess access = Heracles.getRegistryAccess();
        final Registry<Structure> registry = access.registry(Registries.STRUCTURE).orElse(null);
        if (registry != null) {
            for (Structure structure : input) {
                if (structures().is(registry.wrapAsHolder(structure))) {
                    return storage().of(progress, true);
                }
            }
        }
        return storage().of(progress, false);
    }

    @Override
    public float getProgress(ByteTag progress) {
        return storage().readBoolean(progress) ? 1.0F : 0.0F;
    }

    @Override
    public BooleanTaskStorage storage() {
        return BooleanTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<FindStructureTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<FindStructureTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "find_structure");
        }

        @Override
        public Codec<FindStructureTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RegistryValue.codec(Registries.STRUCTURE).fieldOf("structures").forGetter(FindStructureTask::structures)
            ).apply(instance, FindStructureTask::new));
        }
    }
}
