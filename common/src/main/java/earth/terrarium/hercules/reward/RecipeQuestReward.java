package earth.terrarium.hercules.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Objects;

public record RecipeQuestReward(List<ResourceLocation> recipes) implements QuestReward {
    public static final MapCodec<RecipeQuestReward> CODEC = ResourceLocation.CODEC.listOf().fieldOf("recipes").xmap(RecipeQuestReward::new, RecipeQuestReward::recipes);

    @Override
    public void reward(ServerPlayer player) {
        player.awardRecipesByKey(recipes().toArray(new ResourceLocation[0]));
    }

    @Override
    public Codec<? extends QuestReward> codec() {
        return CODEC.codec();
    }
}
