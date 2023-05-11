package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;

public record EnterDimensionTask(String id,
                                 HolderSet<LevelStem> dimensions) implements QuestTask<Level, ByteTag, EnterDimensionTask> {

    public static final QuestTaskType<EnterDimensionTask> TYPE = new Type();
    private static final Codec<HolderSet<LevelStem>> DIMENSION_LIST_CODEC = RegistryCodecs.homogeneousList(Registries.LEVEL_STEM, LevelStem.CODEC, true);

    @Override
    public ByteTag test(ByteTag progress, Level input) {
        return storage().of(progress, dimensions.contains(input
            .registryAccess()
            .registryOrThrow(Registries.LEVEL_STEM)
            .getHolderOrThrow(Registries.levelToLevelStem(input.dimension()))
        ));
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
    public QuestTaskType<EnterDimensionTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<EnterDimensionTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "enter_dimension");
        }

        @Override
        public Codec<EnterDimensionTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                DIMENSION_LIST_CODEC.fieldOf("dimensions").forGetter(EnterDimensionTask::dimensions)
            ).apply(instance, EnterDimensionTask::new));
        }
    }
}
