package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record EnterDimensionTask(
    String id, RegistryValue<ResourceKey<Level>, Level> dimensions
) implements QuestTask<Level, ByteTag, EnterDimensionTask> {

    public static final QuestTaskType<EnterDimensionTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Level input) {
        final RegistryAccess access = input.registryAccess();
        return storage().of(progress, dimensions.is(input.dimension(), (key, tag) -> {
            Registry<Level> registry = access.registry(Registries.DIMENSION).orElse(null);
            if (registry == null) return false;
            Holder<Level> holder = registry.getHolder(key).orElse(null);
            if (holder == null) return false;
            HolderSet.Named<Level> holderSet = registry.getTag(tag).orElse(null);
            if (holderSet == null) return false;
            return holderSet.contains(holder);
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
                RegistryValue.codec(Level.RESOURCE_KEY_CODEC, Registries.DIMENSION).fieldOf("dimensions").forGetter(EnterDimensionTask::dimensions)
            ).apply(instance, EnterDimensionTask::new));
        }
    }
}
