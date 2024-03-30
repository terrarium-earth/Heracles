package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.CollectionType;
import earth.terrarium.heracles.api.tasks.PairQuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.IntegerTaskStorage;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record XpTask(
    String id, String title, QuestIcon<?> icon, int target, XpType xpType, CollectionType collectionType
) implements PairQuestTask<Player, XpTask.Cause, NumericTag, XpTask>, CustomizableQuestElement {

    public static final QuestTaskType<XpTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, Player input, Cause cause) {
        if (collectionType == CollectionType.AUTOMATIC) {
            return storage().set(
                Math.max(storage().readInt(progress),
                    xpType == XpType.LEVEL ? input.experienceLevel : input.totalExperience));
        } else if (collectionType == CollectionType.CONSUME || cause == Cause.MANUALLY_COMPLETED) {
            return deductXp(progress, input);
        }
        return progress;
    }

    @Override
    public NumericTag init(QuestTaskType<?> type, NumericTag progress, ServerPlayer player) {
        if (collectionType != CollectionType.MANUAL) {
            return test(type, progress, player, Cause.GAINED_XP);
        }
        return progress;
    }

    private NumericTag deductXp(NumericTag progress, Player input) {
        if (xpType == XpType.LEVEL && input.experienceLevel >= target()) {
            input.giveExperienceLevels(-target());
            return storage().set(target());
        } else if (xpType == XpType.POINTS && input.totalExperience >= target()) {
            input.giveExperiencePoints(-target());
            return storage().set(target());
        }
        return progress;
    }

    @Override
    public float getProgress(NumericTag progress) {
        return storage().readInt(progress) / (float) target();
    }

    @Override
    public IntegerTaskStorage storage() {
        return IntegerTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<XpTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<XpTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "xp");
        }

        @Override
        public Codec<XpTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.optionalFieldOf("title", "").forGetter(XpTask::title),
                QuestIcons.CODEC.optionalFieldOf("icon", ItemQuestIcon.AIR).forGetter(XpTask::icon),
                Codec.INT.fieldOf("amount").orElse(1).forGetter(XpTask::target),
                EnumCodec.of(XpType.class).fieldOf("xpType").orElse(XpType.LEVEL).forGetter(XpTask::xpType),
                EnumCodec.of(CollectionType.class).fieldOf("collectionType").orElse(CollectionType.CONSUME).forGetter(XpTask::collectionType)
            ).apply(instance, XpTask::new));
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

    public enum Cause {
        GAINED_XP,
        MANUALLY_COMPLETED,
    }
}
