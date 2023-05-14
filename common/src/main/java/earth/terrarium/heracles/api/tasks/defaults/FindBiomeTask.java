package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public record FindBiomeTask(String id,
                            HolderSet<Biome> biomes) implements QuestTask<Holder<Biome>, ByteTag, FindBiomeTask> {

    public static final QuestTaskType<FindBiomeTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Holder<Biome> input) {
        return storage().of(progress, biomes.contains(input));
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
    public QuestTaskType<FindBiomeTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<FindBiomeTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "find_biome");
        }

        @Override
        public Codec<FindBiomeTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Biome.LIST_CODEC.fieldOf("dimensions").forGetter(FindBiomeTask::biomes)
            ).apply(instance, FindBiomeTask::new));
        }
    }
}
