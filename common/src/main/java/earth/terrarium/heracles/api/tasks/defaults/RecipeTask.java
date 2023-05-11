package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Set;

public record RecipeTask(String id, Set<ResourceLocation> recipes) implements QuestTask<Recipe<?>, ByteTag, RecipeTask> {

    public static final QuestTaskType<RecipeTask> TYPE = new Type();

    @Override
    public ByteTag test(ByteTag progress, Recipe<?> input) {
        return storage().of(progress, recipes.contains(input.getId()));
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
    public QuestTaskType<RecipeTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<RecipeTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "recipe");
        }

        @Override
        public Codec<RecipeTask> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(RecipeTask::id),
                CodecExtras.set(ResourceLocation.CODEC).fieldOf("recipes").forGetter(RecipeTask::recipes)
            ).apply(instance, RecipeTask::new));
        }
    }
}
