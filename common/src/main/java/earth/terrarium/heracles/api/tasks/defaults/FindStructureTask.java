package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

public record FindStructureTask(String id, HolderSet<Structure> structures) implements QuestTask<Holder<Structure>, FindStructureTask> {

    public static final QuestTaskType<FindStructureTask> TYPE = new Type();
    public static final Codec<HolderSet<Structure>> STRUCTURE_LIST_CODEC = RegistryCodecs.homogeneousList(Registries.STRUCTURE, Structure.DIRECT_CODEC, true);

    @Override
    public int target() {
        return 1;
    }

    @Override
    public int test(Holder<Structure> input) {
        return structures.contains(input) ? 1 : 0;
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
