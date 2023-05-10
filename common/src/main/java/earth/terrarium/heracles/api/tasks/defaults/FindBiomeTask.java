package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public record FindBiomeTask(String id, HolderSet<Biome> biomes) implements QuestTask<Holder<Biome>, FindBiomeTask> {

    public static final QuestTaskType<FindBiomeTask> TYPE = new Type();

    @Override
    public int target() {
        return 1;
    }

    @Override
    public int test(Holder<Biome> input) {
        return biomes.contains(input) ? 1 : 0;
    }

    @Override
    public QuestTaskType<FindBiomeTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<FindBiomeTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "find_biome");
        }

        @Override
        public Codec<FindBiomeTask> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(FindBiomeTask::id),
                Biome.LIST_CODEC.fieldOf("dimensions").forGetter(FindBiomeTask::biomes)
            ).apply(instance, FindBiomeTask::new));
        }
    }
}
