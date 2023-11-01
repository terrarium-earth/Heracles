package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.EnumCodec;
import earth.terrarium.heracles.common.utils.ModUtils;

import java.util.Objects;

public final class QuestSettings {
    public static final Codec<QuestSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.fieldOf("individual_progress").orElse(false).forGetter(QuestSettings::individualProgress),
        EnumCodec.of(ModUtils.QuestStatus.class).orElse(ModUtils.QuestStatus.LOCKED).fieldOf("hidden").forGetter(QuestSettings::hiddenUntil),
        Codec.BOOL.fieldOf("unlockNotification").orElse(false).forGetter(QuestSettings::unlockNotification),
        Codec.BOOL.fieldOf("showDependencyArrow").orElse(true).forGetter(QuestSettings::showDependencyArrow)
    ).apply(instance, QuestSettings::new));

    private boolean individualProgress;
    private ModUtils.QuestStatus hiddenUntil;
    private boolean unlockNotification;
    private boolean showDependencyArrow;

    public QuestSettings(boolean individualProgress, ModUtils.QuestStatus hiddenUntil, boolean unlockNotification, boolean showDependencyArrow) {
        this.individualProgress = individualProgress;
        this.hiddenUntil = hiddenUntil;
        this.unlockNotification = unlockNotification;
        this.showDependencyArrow = showDependencyArrow;
    }

    public static QuestSettings createDefault() {
        return new QuestSettings(false, ModUtils.QuestStatus.LOCKED, false, true);
    }

    public boolean individualProgress() {
        return individualProgress;
    }

    public ModUtils.QuestStatus hiddenUntil() {
        return hiddenUntil;
    }

    public boolean unlockNotification() {
        return unlockNotification;
    }

    public boolean showDependencyArrow() {
        return showDependencyArrow;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (QuestSettings) obj;
        return
            this.individualProgress == that.individualProgress &&
            this.hiddenUntil == that.hiddenUntil &&
            this.unlockNotification == that.unlockNotification &&
            this.showDependencyArrow == that.showDependencyArrow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(individualProgress, hiddenUntil, unlockNotification, showDependencyArrow);
    }

    public void update(QuestSettings newSettings) {
        this.individualProgress = newSettings.individualProgress;
        this.hiddenUntil = newSettings.hiddenUntil;
        this.unlockNotification = newSettings.unlockNotification;
        this.showDependencyArrow = newSettings.showDependencyArrow;
    }

    public void setIndividualProgress(boolean individualProgress) {
        this.individualProgress = individualProgress;
    }

    public void setHiddenUntil(ModUtils.QuestStatus hiddenUntil) {
        this.hiddenUntil = hiddenUntil;
    }

    public void setUnlockNotification(boolean unlockNotification) {
        this.unlockNotification = unlockNotification;
    }

    public void setShowDependencyArrow(boolean showDependencyArrow) {
        this.showDependencyArrow = showDependencyArrow;
    }

}
