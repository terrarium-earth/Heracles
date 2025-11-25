package earth.terrarium.heracles.api.rewards.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import earth.terrarium.heracles.api.rewards.RewardUtils;
import earth.terrarium.heracles.common.utils.ItemStackCodec;
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
            return ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "item");
        }

        @Override
        public MapCodec<ItemReward> codec(String id) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.lenientOptionalFieldOf("title", "").forGetter(ItemReward::title),
                QuestIcons.CODEC.lenientOptionalFieldOf("icon", ItemQuestIcon.AIR).forGetter(ItemReward::icon),
                ItemStackCodec.CODEC.lenientOptionalFieldOf("item", Items.AIR.getDefaultInstance()).forGetter(ItemReward::stack)
            ).apply(instance, ItemReward::new));
        }
    }
}
