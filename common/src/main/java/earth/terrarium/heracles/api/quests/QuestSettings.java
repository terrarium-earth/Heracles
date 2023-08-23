package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public final class QuestSettings {

    public static final Codec<QuestSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.fieldOf("individual_progress").orElse(false).forGetter(QuestSettings::individualProgress),
        Codec.BOOL.fieldOf("hidden").orElse(false).forGetter(QuestSettings::hidden)
    ).apply(instance, QuestSettings::new));

    private boolean individualProgress;
    private boolean hidden;

    public QuestSettings(boolean individualProgress, boolean hidden) {
        this.individualProgress = individualProgress;
        this.hidden = hidden;
    }

    public static QuestSettings createDefault() {
        return new QuestSettings(false, false);
    }

    public boolean individualProgress() {
        return individualProgress;
    }

    public boolean hidden() {
        return hidden;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (QuestSettings) obj;
        return
            this.individualProgress == that.individualProgress &&
                this.hidden == that.hidden;
    }

    @Override
    public int hashCode() {
        return Objects.hash(individualProgress, hidden);
    }

    public void update(QuestSettings newSettings) {
        this.individualProgress = newSettings.individualProgress;
        this.hidden = newSettings.hidden;
    }

    public void setIndividualProgress(boolean individualProgress) {
        this.individualProgress = individualProgress;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
