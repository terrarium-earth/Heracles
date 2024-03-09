package earth.terrarium.heracles.common.network.packets.quests.data;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplayStatus;
import earth.terrarium.heracles.api.quests.QuestSettings;

import java.util.Optional;

public record NetworkQuestSettingsData(
    Optional<Boolean> individualProgress,
    Optional<QuestDisplayStatus> hiddenUntil,
    Optional<Boolean> unlockNotification,
    Optional<Boolean> showDependencyArrow,
    Optional<Boolean> repeatable
) {

    public static final ByteCodec<NetworkQuestSettingsData> CODEC = ObjectByteCodec.create(
        ByteCodec.BOOLEAN.optionalFieldOf(NetworkQuestSettingsData::individualProgress),
        ByteCodec.ofEnum(QuestDisplayStatus.class).optionalFieldOf(NetworkQuestSettingsData::hiddenUntil),
        ByteCodec.BOOLEAN.optionalFieldOf(NetworkQuestSettingsData::unlockNotification),
        ByteCodec.BOOLEAN.optionalFieldOf(NetworkQuestSettingsData::showDependencyArrow),
        ByteCodec.BOOLEAN.optionalFieldOf(NetworkQuestSettingsData::repeatable),
        NetworkQuestSettingsData::new
    );

    public void update(Quest quest) {
        QuestSettings settings = quest.settings();
        individualProgress.ifPresent(settings::setIndividualProgress);
        hiddenUntil.ifPresent(settings::setHiddenUntil);
        unlockNotification.ifPresent(settings::setUnlockNotification);
        showDependencyArrow.ifPresent(settings::setShowDependencyArrow);
        repeatable.ifPresent(settings::setRepeatable);
    }
}
