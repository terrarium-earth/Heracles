package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public class QuestSettings {

    public static final Codec<QuestSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.fieldOf("individual_progress").orElse(false).forGetter(QuestSettings::individualProgress)
    ).apply(instance, QuestSettings::new));

    private boolean individualProgress;

    public QuestSettings(boolean individualProgress) {
        this.individualProgress = individualProgress;
    }

    public static QuestSettings createDefault() {
        return new QuestSettings(false);
    }

    public boolean individualProgress() {
        return individualProgress;
    }

    public void setIndividualProgress(boolean individualProgress) {
        this.individualProgress = individualProgress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (QuestSettings) obj;
        return this.individualProgress == that.individualProgress;
    }

    @Override
    public int hashCode() {
        return Objects.hash(individualProgress);
    }

    @Override
    public String toString() {
        return "QuestSettings[" +
            "individualProgress=" + individualProgress + ']';
    }

    public void update(QuestSettings newSettings) {
        this.individualProgress = newSettings.individualProgress;
    }

}
