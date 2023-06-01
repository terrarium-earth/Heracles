package earth.terrarium.heracles.common.menus.quest;

import com.teamresourceful.resourcefullib.common.menu.MenuContent;
import com.teamresourceful.resourcefullib.common.menu.MenuContentSerializer;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record QuestContent(
    String id, String fromGroup, QuestProgress progress, Map<String, ModUtils.QuestStatus> quests
) implements MenuContent<QuestContent> {

    public static final MenuContentSerializer<QuestContent> SERIALIZER = new Serializer();

    @Override
    public MenuContentSerializer<QuestContent> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements MenuContentSerializer<QuestContent> {

        @Override
        public @Nullable QuestContent from(FriendlyByteBuf buffer) {
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

        @Override
        public void to(FriendlyByteBuf buffer, QuestContent content) {
            buffer.writeUtf(content.id());
            buffer.writeUtf(content.fromGroup());
            buffer.writeNbt(content.progress().save());
            buffer.writeVarInt(content.quests.size());
            for (var entry : content.quests.entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeEnum(entry.getValue());
            }
        }
    }
}
