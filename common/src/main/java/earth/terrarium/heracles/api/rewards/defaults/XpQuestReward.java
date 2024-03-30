package earth.terrarium.heracles.api.rewards.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.rewards.QuestReward;
import earth.terrarium.heracles.api.rewards.QuestRewardType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record XpQuestReward(String id, String title, QuestIcon<?> icon, XpType xpType, int amount) implements QuestReward<XpQuestReward>, CustomizableQuestElement {

    public static final QuestRewardType<XpQuestReward> TYPE = new Type();

    @Override
    public Stream<ItemStack> reward(ServerPlayer player) {
        return switch (xpType()) {
            case LEVEL -> {
                player.giveExperienceLevels(amount());
                yield Stream.of();
            }
            case POINTS -> {
                player.giveExperiencePoints(amount());
                yield Stream.of();
            }
        };
    }

    @Override
    public QuestRewardType<XpQuestReward> type() {
        return TYPE;
    }

    private static class Type implements QuestRewardType<XpQuestReward> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "xp");
        }

        @Override
        public Codec<XpQuestReward> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.optionalFieldOf("title", "").forGetter(XpQuestReward::title),
                QuestIcons.CODEC.optionalFieldOf("icon", ItemQuestIcon.AIR).forGetter(XpQuestReward::icon),
                EnumCodec.of(XpType.class).fieldOf("xptype").orElse(XpType.LEVEL).forGetter(XpQuestReward::xpType),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(XpQuestReward::amount)
            ).apply(instance, XpQuestReward::new));
        }
    }

    public enum XpType implements StringRepresentable {
        LEVEL,
        POINTS;

        public Component text() {
            return switch (this) {
                case LEVEL -> Component.translatable("reward.heracles.xp.type.level");
                case POINTS -> Component.translatable("reward.heracles.xp.type.point");
            };
        }

        @Override
        public @NotNull String getSerializedName() {
            return text().getString();
        }
    }
}
