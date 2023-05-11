package earth.terrarium.heracles.api.rewards.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import com.teamresourceful.resourcefullib.common.collections.WeightedCollection;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.RewardUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record WeightedItemReward(String id, WeightedCollection<WeightedStack> items,
                                 int rolls) implements QuestReward<WeightedItemReward> {

    public static final QuestRewardType<WeightedItemReward> TYPE = new Type();

    @Override
    public Stream<ItemStack> reward(ServerPlayer player) {
        List<ItemStack> items = new ArrayList<>(rolls);
        for (int i = 0; i < rolls; i++) {
            items.add(this.items.next().stack.copy());
        }
        items.forEach(stack -> RewardUtils.giveItem(player, stack));
        return items.stream();
    }

    @Override
    public QuestRewardType<WeightedItemReward> type() {
        return TYPE;
    }

    private static class Type implements QuestRewardType<WeightedItemReward> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "weighted_item");
        }

        @Override
        public Codec<WeightedItemReward> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                CodecExtras.weightedCollection(WeightedStack.CODEC, WeightedStack::weight).fieldOf("items").forGetter(WeightedItemReward::items),
                ExtraCodecs.POSITIVE_INT.fieldOf("rolls").orElse(1).forGetter(WeightedItemReward::rolls)
            ).apply(instance, WeightedItemReward::new));
        }
    }

    public record WeightedStack(ItemStack stack, double weight) {

        public static final Codec<WeightedStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackCodec.CODEC.fieldOf("item").forGetter(WeightedStack::stack),
            Codec.DOUBLE.fieldOf("weight").orElse(1d).forGetter(WeightedStack::weight)
        ).apply(instance, WeightedStack::new));
    }
}
