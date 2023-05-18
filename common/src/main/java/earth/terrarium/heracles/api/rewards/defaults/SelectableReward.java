package earth.terrarium.heracles.api.rewards.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.QuestRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public record SelectableReward(String id, int amount,
                               Map<String, QuestReward<?>> rewards) implements QuestReward<SelectableReward> {

    public static final Type TYPE = new Type();

    public SelectableReward {
        if (rewards.isEmpty()) {
            throw new IllegalArgumentException("Selectable rewards must have at least one reward");
        }
        for (var entry : rewards.entrySet()) {
            if (!entry.getValue().canBeMassClaimed()) {
                throw new IllegalArgumentException("Selectable rewards contains a reward '" + entry.getKey() + "' that cannot be mass claimed");
            }
        }
    }

    @Override
    public boolean canBeMassClaimed() {
        return false;
    }

    public Stream<ItemStack> reward(ServerPlayer player, List<String> selected) {
        if (selected.isEmpty() || selected.size() > amount) {
            return Stream.empty();
        }
        return selected.stream()
            .map(rewards::get)
            .filter(Objects::nonNull)
            .flatMap(reward -> reward.reward(player));
    }

    @Override
    public Stream<ItemStack> reward(ServerPlayer player) {
        return Stream.empty();
    }

    @Override
    public QuestRewardType<SelectableReward> type() {
        return TYPE;
    }

    private static class Type implements QuestRewardType<SelectableReward> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "selectable");
        }

        @Override
        public Codec<SelectableReward> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                ExtraCodecs.POSITIVE_INT.fieldOf("amount").orElse(1).forGetter(SelectableReward::amount),
                QuestRewards.CODEC.fieldOf("rewards").forGetter(SelectableReward::rewards)
            ).apply(instance, SelectableReward::new));
        }
    }
}
