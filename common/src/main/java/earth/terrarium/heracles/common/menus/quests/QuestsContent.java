package earth.terrarium.heracles.common.menus.quests;

import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record QuestsContent(
    String group,
    Map<String, ModUtils.QuestStatus> quests,
    boolean canEdit
) implements MenuContent<QuestsContent> {

    public static final MenuContentSerializer<QuestsContent> SERIALIZER = new Serializer();

    @Override
    public MenuContentSerializer<QuestsContent> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements MenuContentSerializer<QuestsContent> {

        @Override
        public @Nullable QuestsContent from(FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Map<String, ModUtils.QuestStatus> quests = new HashMap<>();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                quests.put(buffer.readUtf(), buffer.readEnum(ModUtils.QuestStatus.class));
            }
            return new QuestsContent(group, quests, buffer.readBoolean());
        }

        @Override
        public void to(FriendlyByteBuf buffer, QuestsContent content) {
            buffer.writeUtf(content.group);
            buffer.writeVarInt(content.quests.size());
            for (var entry : content.quests.entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeEnum(entry.getValue());
            }
            buffer.writeBoolean(content.canEdit());
        }
    }
}
