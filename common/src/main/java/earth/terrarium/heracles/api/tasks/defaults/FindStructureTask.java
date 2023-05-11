package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Collection;

public record FindStructureTask(String id,
                                HolderSet<Structure> structures) implements QuestTask<Collection<Structure>, ByteTag, FindStructureTask> {

    public static final QuestTaskType<FindStructureTask> TYPE = new Type();
    public static final Codec<HolderSet<Structure>> STRUCTURE_LIST_CODEC = RegistryCodecs.homogeneousList(Registries.STRUCTURE, Structure.DIRECT_CODEC, true);

    @Override
    public ByteTag test(ByteTag progress, Collection<Structure> input) {
        return storage().of(progress, input.stream().anyMatch(structure -> {
            Registry<Structure> structuresRegistry = Heracles.getRegistryAccess().registryOrThrow(Registries.STRUCTURE);
            return structures.contains(structuresRegistry.getResourceKey(structure).flatMap(structuresRegistry::getHolder).orElseThrow());
        }));
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
        public Codec<FindStructureTask> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(FindStructureTask::id),
                STRUCTURE_LIST_CODEC.fieldOf("structures").forGetter(FindStructureTask::structures)
            ).apply(instance, FindStructureTask::new));
        }
    }
}
