package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record RecipeTask(
    String id, String title, QuestIcon<?> icon, Set<ResourceLocation> recipes
) implements QuestTask<Recipe<?>, ByteTag, RecipeTask>, CustomizableQuestElement {

    public static final QuestTaskType<RecipeTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Recipe<?> input) {
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

    public List<Component> titles() {
        List<Component> titles = new ArrayList<>();
        for (ResourceLocation id : recipes()) {
            titles.add(Component.translatableWithFallback(
                "recipes." + id.toLanguageKey().replace('/', '.'),
                id.toString()
            ));
        }
        titles.removeIf(Objects::isNull);
        return titles;
    }

    private static class Type implements QuestTaskType<RecipeTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "recipe");
        }

        @Override
        public Codec<RecipeTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("title").orElse("").forGetter(RecipeTask::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(RecipeTask::icon),
                CodecExtras.set(ResourceLocation.CODEC).fieldOf("recipes").forGetter(RecipeTask::recipes)
            ).apply(instance, RecipeTask::new));
        }
    }
}
