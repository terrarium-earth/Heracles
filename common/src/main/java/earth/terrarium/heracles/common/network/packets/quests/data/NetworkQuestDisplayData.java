package earth.terrarium.heracles.common.network.packets.quests.data;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import earth.terrarium.heracles.api.quests.GroupDisplay;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public record NetworkQuestDisplayData(
    Optional<QuestIcon<?>> icon,
    Optional<ResourceLocation> background,
    Optional<Component> title,
    Optional<Component> subtitle,
    Optional<List<String>> description,
    Optional<Map<String, GroupDisplay>> groups
) {

    private static final ByteCodec<Map<String, GroupDisplay>> GROUPS_CODEC = GroupDisplay.BYTE_CODEC.listOf()
        .map(
            list -> Util.make(new HashMap<>(), map -> list.forEach(item -> map.put(item.id(), item))),
            map -> new ArrayList<>(map.values())
        );

    public static final ByteCodec<NetworkQuestDisplayData> CODEC = ObjectByteCodec.create(
        QuestIcons.BYTE_CODEC.optionalFieldOf(NetworkQuestDisplayData::icon),
        ExtraByteCodecs.RESOURCE_LOCATION.optionalFieldOf(NetworkQuestDisplayData::background),
        ExtraByteCodecs.COMPONENT.optionalFieldOf(NetworkQuestDisplayData::title),
        ExtraByteCodecs.COMPONENT.optionalFieldOf(NetworkQuestDisplayData::subtitle),
        ByteCodec.STRING.listOf().optionalFieldOf(NetworkQuestDisplayData::description),
        GROUPS_CODEC.optionalFieldOf(NetworkQuestDisplayData::groups),
        NetworkQuestDisplayData::new
    );

    public void update(Quest quest) {
        icon.ifPresent(quest.display()::setIcon);
        background.ifPresent(quest.display()::setIconBackground);
        title.ifPresent(quest.display()::setTitle);
        subtitle.ifPresent(quest.display()::setSubtitle);
        description.ifPresent(quest.display()::setDescription);
        groups.ifPresent(groups -> {
            quest.display().groups().clear();
            quest.display().groups().putAll(groups);
        });
    }
}
