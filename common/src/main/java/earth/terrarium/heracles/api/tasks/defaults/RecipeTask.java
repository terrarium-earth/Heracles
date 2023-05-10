package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Set;

public record RecipeTask(String id, Set<ResourceLocation> recipes) implements QuestTask<Recipe<?>, RecipeTask> {

    public static final QuestTaskType<RecipeTask> TYPE = new Type();

    @Override
    public int target() {
        return 1;
    }

    @Override
    public int test(Recipe<?> input) {
        return recipes.contains(input.getId()) ? 1 : 0;
    }

    @Override
    public QuestTaskType<RecipeTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<RecipeTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "advancement");
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
