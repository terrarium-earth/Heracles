package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;

import java.util.Objects;

public final class QuestSettings {
    public static final Codec<QuestSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.fieldOf("individual_progress").orElse(false).forGetter(QuestSettings::individualProgress),
        EnumCodec.of(QuestDisplayStatus.class).fieldOf("hidden").orElse(QuestDisplayStatus.LOCKED).forGetter(QuestSettings::hiddenUntil),
        Codec.BOOL.fieldOf("unlockNotification").orElse(false).forGetter(QuestSettings::unlockNotification),
        Codec.BOOL.fieldOf("showDependencyArrow").orElse(true).forGetter(QuestSettings::showDependencyArrow),
        Codec.BOOL.fieldOf("repeatable").orElse(false).forGetter(QuestSettings::repeatable),
        Codec.BOOL.fieldOf("autoClaimRewards").orElse(false).forGetter(QuestSettings::autoClaimRewards)
    ).apply(instance, QuestSettings::new));

    private boolean individualProgress;
    private QuestDisplayStatus hiddenUntil;
    private boolean unlockNotification;
    private boolean showDependencyArrow;
    private boolean repeatable;
    private boolean autoClaimRewards;

    public QuestSettings(boolean individualProgress, QuestDisplayStatus hiddenUntil, boolean unlockNotification, boolean showDependencyArrow, boolean repeatable, boolean autoClaimRewards) {
        this.individualProgress = individualProgress;
        this.hiddenUntil = hiddenUntil;
        this.unlockNotification = unlockNotification;
        this.showDependencyArrow = showDependencyArrow;
        this.repeatable = repeatable;
        this.autoClaimRewards = autoClaimRewards;
    }

    public static QuestSettings createDefault() {
        return new QuestSettings(false, QuestDisplayStatus.LOCKED, false, true, false, false);
    }

    public boolean individualProgress() {
        return individualProgress;
    }

    public QuestDisplayStatus hiddenUntil() {
        return hiddenUntil;
    }

    public boolean unlockNotification() {
        return unlockNotification;
    }

    public boolean showDependencyArrow() {
        return showDependencyArrow;
    }

    public Boolean repeatable() {
        return repeatable;
    }

    public boolean autoClaimRewards() {
        return autoClaimRewards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestSettings that = (QuestSettings) o;
        return individualProgress == that.individualProgress &&
               unlockNotification == that.unlockNotification &&
               showDependencyArrow == that.showDependencyArrow &&
               repeatable == that.repeatable &&
               autoClaimRewards == that.autoClaimRewards &&
               hiddenUntil == that.hiddenUntil;
    }

    @Override
    public int hashCode() {
        return Objects.hash(individualProgress, hiddenUntil, unlockNotification, showDependencyArrow, repeatable, autoClaimRewards);
    }

    public void update(QuestSettings newSettings) {
        this.individualProgress = newSettings.individualProgress;
        this.hiddenUntil = newSettings.hiddenUntil;
        this.unlockNotification = newSettings.unlockNotification;
        this.showDependencyArrow = newSettings.showDependencyArrow;
        this.repeatable = newSettings.repeatable;
    }

    public void setIndividualProgress(boolean individualProgress) {
        this.individualProgress = individualProgress;
    }

    public void setHiddenUntil(QuestDisplayStatus hiddenUntil) {
        this.hiddenUntil = hiddenUntil;
    }

    public void setUnlockNotification(boolean unlockNotification) {
        this.unlockNotification = unlockNotification;
    }

    public void setShowDependencyArrow(boolean showDependencyArrow) {
        this.showDependencyArrow = showDependencyArrow;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public void setAutoClaimRewards(boolean autoClaimRewards) {
        this.autoClaimRewards = autoClaimRewards;
    }

}
