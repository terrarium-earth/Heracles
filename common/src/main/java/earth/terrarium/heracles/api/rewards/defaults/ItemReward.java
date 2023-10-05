package earth.terrarium.heracles.api.rewards.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.recipes.ItemStackCodec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.RewardUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.stream.Stream;

public record ItemReward(String id, String title, QuestIcon<?> icon, ItemStack stack) implements QuestReward<ItemReward>, CustomizableQuestElement {

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
        public Codec<ItemReward> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("title").orElse("").forGetter(ItemReward::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(ItemReward::icon),
                ItemStackCodec.CODEC.fieldOf("item").forGetter(ItemReward::stack)
            ).apply(instance, ItemReward::new));
        }
    }
}
