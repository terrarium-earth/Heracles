package earth.terrarium.heracles.api.rewards.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.RewardUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public record ItemReward(String id, ItemStack stack) implements QuestReward<ItemReward> {

    public static final QuestRewardType<ItemReward> TYPE = new Type();

    @Override
    public Stream<ItemStack> reward(ServerPlayer player) {
        RewardUtils.giveItem(player, stack.copy());
        return Stream.of(stack.copy());
    }

    @Override
    public QuestRewardType<ItemReward> type() {
        return TYPE;
    }

    private static class Type implements QuestRewardType<ItemReward> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "item");
        }

        @Override
        public Codec<ItemReward> codec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(ItemReward::id),
                ItemStackCodec.CODEC.fieldOf("item").forGetter(ItemReward::stack)
            ).apply(instance, ItemReward::new));
        }
    }
}
