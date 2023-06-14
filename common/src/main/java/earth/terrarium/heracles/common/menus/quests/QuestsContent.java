package earth.terrarium.heracles.common.menus.quests;

import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public record QuestsContent(
    String group,
    Map<String, ModUtils.QuestStatus> quests,
    boolean canEdit
) {

    public static QuestsContent from(FriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        Map<String, ModUtils.QuestStatus> quests = new HashMap<>();
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            quests.put(buffer.readUtf(), buffer.readEnum(ModUtils.QuestStatus.class));
        }
        return new QuestsContent(group, quests, buffer.readBoolean());
    }

    public void to(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.group);
        buffer.writeVarInt(this.quests.size());
        for (var entry : this.quests.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeEnum(entry.getValue());
        }
        buffer.writeBoolean(this.canEdit());
    }
}
