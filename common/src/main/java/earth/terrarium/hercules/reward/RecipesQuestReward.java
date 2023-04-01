package earth.terrarium.hercules.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record RecipesQuestReward(List<ResourceLocation> recipes) implements QuestReward {
    public static final String KEY = "recipes";

    public static final MapCodec<RecipesQuestReward> MAP_CODEC = ResourceLocation.CODEC.listOf().fieldOf("recipes").xmap(RecipesQuestReward::new, RecipesQuestReward::recipes);

    @Override
    public Stream<Item> reward(ServerPlayer player) {
        player.awardRecipesByKey(recipes().toArray(new ResourceLocation[0]));

        return recipes.stream().map(player.server.getRecipeManager()::byKey)
                .flatMap(Optional::stream)
                .flatMap(recipe -> Stream.of(recipe.getToastSymbol().getItem(), recipe.getResultItem().getItem()));
    }

    @Override
    public Codec<? extends QuestReward> codec() {
        return MAP_CODEC.codec();
    }
}
