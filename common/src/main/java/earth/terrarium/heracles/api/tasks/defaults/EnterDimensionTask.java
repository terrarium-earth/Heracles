package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

public record EnterDimensionTask(String id, HolderSet<LevelStem> dimensions) implements QuestTask<Level, EnterDimensionTask> {

    public static final QuestTaskType<EnterDimensionTask> TYPE = new Type();
    private static final Codec<HolderSet<LevelStem>> DIMENSION_LIST_CODEC = RegistryCodecs.homogeneousList(Registries.LEVEL_STEM, LevelStem.CODEC, true);

    @Override
    public int target() {
        return 1;
    }

    @Override
    public int test(Level input) {
        return dimensions.contains(input
                .registryAccess()
                .registryOrThrow(Registries.LEVEL_STEM)
                .getHolderOrThrow(Registries.levelToLevelStem(input.dimension()))
        ) ? 1 : 0;
    }

    @Override
    public QuestTaskType<EnterDimensionTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<EnterDimensionTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "enter_dimension");
        }

        @Override
        public Codec<EnterDimensionTask> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("id").forGetter(EnterDimensionTask::id),
                    DIMENSION_LIST_CODEC.fieldOf("dimensions").forGetter(EnterDimensionTask::dimensions)
            ).apply(instance, EnterDimensionTask::new));
        }
    }
}
