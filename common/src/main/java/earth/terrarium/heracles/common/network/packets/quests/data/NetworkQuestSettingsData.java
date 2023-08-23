package earth.terrarium.heracles.common.network.packets.quests.data;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestSettings;

import java.util.Optional;

public record NetworkQuestSettingsData(
    Optional<Boolean> individualProgress,
    Optional<Boolean> hidden
) {

    public static final ByteCodec<NetworkQuestSettingsData> CODEC = ObjectByteCodec.create(
        ByteCodec.BOOLEAN.optionalFieldOf(NetworkQuestSettingsData::individualProgress),
        ByteCodec.BOOLEAN.optionalFieldOf(NetworkQuestSettingsData::hidden),
        NetworkQuestSettingsData::new
    );

    public void update(Quest quest) {
        QuestSettings settings = quest.settings();
        individualProgress.ifPresent(settings::setIndividualProgress);
        hidden.ifPresent(settings::setHidden);
    }
}
