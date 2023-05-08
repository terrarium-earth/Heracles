package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record QuestSettings(
    boolean individualProgress
) {

    public static final Codec<QuestSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.fieldOf("individual_progress").orElse(false).forGetter(QuestSettings::individualProgress)
    ).apply(instance, QuestSettings::new));

    public static QuestSettings createDefault() {
        return new QuestSettings(false);
    }
}
