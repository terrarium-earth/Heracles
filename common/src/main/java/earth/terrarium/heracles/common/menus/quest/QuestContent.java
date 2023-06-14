package earth.terrarium.heracles.common.menus.quest;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public record QuestContent(
    String id, String fromGroup, QuestProgress progress, Map<String, ModUtils.QuestStatus> quests
) {

    public static QuestContent from(FriendlyByteBuf buffer) {
        String id = buffer.readUtf();
        String fromGroup = buffer.readUtf();
        Quest quest = ClientQuests.get(id).map(ClientQuests.QuestEntry::value).orElse(null);
        CompoundTag tag = buffer.readNbt();
        QuestProgress progress = new QuestProgress(quest, quest == null ? null : tag);
        Map<String, ModUtils.QuestStatus> quests = new HashMap<>();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            quests.put(buffer.readUtf(), buffer.readEnum(ModUtils.QuestStatus.class));
        }
        return new QuestContent(id, fromGroup, progress, quests);
    }

    public void to(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.id());
        buffer.writeUtf(this.fromGroup());
        buffer.writeNbt(this.progress().save());
        buffer.writeVarInt(this.quests.size());
        for (var entry : this.quests.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeEnum(entry.getValue());
        }
    }
}
